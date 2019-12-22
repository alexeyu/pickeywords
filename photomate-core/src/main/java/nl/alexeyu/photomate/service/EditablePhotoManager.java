package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.LocalPhotoUpdater;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.Configuration;
import nl.alexeyu.photomate.util.MediaFileProcessors;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto>, LocalPhotoUpdater {

    private List<EditablePhoto> photos = new ArrayList<>();

    private LocalPhotoApi<EditablePhoto> photoApi;

    private LocalPhotoApi<EditablePhoto> videoApi;

    private Configuration configuration;

    private EditablePhoto currentPhoto;

    @Inject
    public EditablePhotoManager(@Named("photoApi") LocalPhotoApi<EditablePhoto> photoApi,
                                @Named("videoApi") LocalPhotoApi<EditablePhoto> videoApi,
                                Configuration configuration) {
        this.photoApi = photoApi;
        this.videoApi = videoApi;
        this.configuration = configuration;
    }

    public List<EditablePhoto> createPhotos(Path dir) {
        var photoPaths = MediaFileProcessors.JPEG.apply(dir);
        var videoPaths = MediaFileProcessors.MPEG4.apply(dir);
        this.photos = photoApi.createPhotos(photoPaths, EditablePhoto::new);
        this.photos.addAll(videoApi.createPhotos(videoPaths, EditablePhoto::new));
        configuration.getProperty("copyright").map(PhotoCopyrightSetter::new)
                .ifPresent(creator -> photos.forEach(photo -> photo.addPropertyChangeListener(creator)));
        return photos;
    }

    public List<EditablePhoto> getPhotosReadyToUpload() {
        return photos.stream()
                .filter(EditablePhoto::isReadyToUpload)
                .collect(Collectors.toList());
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (currentPhoto != null && PhotoProperty.has(e.getPropertyName())) {
            photoApi.updateProperty(currentPhoto, PhotoProperty.of(e.getPropertyName()), e.getNewValue());
        }
    }

    @Override
    public void photoSelected(EditablePhoto photo) {
        this.currentPhoto = photo;
    }

    @Override
    public void updateProperty(LocalPhoto photo, PhotoProperty property, Object propertyValue) {
        photoApi.updateProperty(photo, property, propertyValue);
    }

    private class PhotoCopyrightSetter implements PropertyChangeListener {

        private final String creator;

        private PhotoCopyrightSetter(String creator) {
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
