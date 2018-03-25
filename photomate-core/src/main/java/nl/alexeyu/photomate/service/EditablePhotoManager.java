package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.ImageUtils;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private List<EditablePhoto> photos = new ArrayList<>();

    @Inject
    private LocalPhotoApi<EditablePhoto> photoApi;

    @Inject
    private ConfigReader configReader;

    private EditablePhoto currentPhoto;

    public List<EditablePhoto> createPhotos(Path dir) {
        var paths = ImageUtils.getJpegImages(dir);
        this.photos = photoApi.createPhotos(paths, path -> new EditablePhoto(path));
        configReader.getProperty("copyright").ifPresent(creator ->
            photos.forEach(photo -> photo.addPropertyChangeListener(new PhotoCopyrightSetter(creator))));
        return photos;
    }

    public List<EditablePhoto> validatePhotos() throws PhotoNotReadyException {
        var notReadyPhotos = photos.stream()
        		.filter(photo -> !photo.isReadyToUpload())
        		.collect(Collectors.toList());
        if (photos.isEmpty() || !notReadyPhotos.isEmpty()) {
            throw new PhotoNotReadyException(notReadyPhotos);
        }
        return photos;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (currentPhoto != null && PhotoProperty.has(e.getPropertyName())) {
            photoApi.updateProperty(currentPhoto, 
                    PhotoProperty.of(e.getPropertyName()), e.getNewValue());
        }
    }

    @Override
    public void photoSelected(EditablePhoto photo) {
        this.currentPhoto = photo;
    }

    private class PhotoCopyrightSetter implements PropertyChangeListener {

        private final String creator;

        public PhotoCopyrightSetter(String creator) {
            this.creator = creator;
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(METADATA_PROPERTY)) {
                var photo = (LocalPhoto) e.getSource();
                photo.removePropertyChangeListener(this);
                photoApi.updateProperty(photo, PhotoProperty.CREATOR, creator);
            }
        }

    }

}
