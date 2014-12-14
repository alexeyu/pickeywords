package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.api.editable.EditablePhotoFactory;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.ConfigReader;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private List<EditablePhoto> photos = new ArrayList<>();

    @Inject
    private LocalPhotoApi<EditablePhoto> photoApi;

    @Inject
    private ConfigReader configReader;

    private PhotoCopyrightSetter photoCopyrightSetter;
    
    private Optional<EditablePhoto> currentPhoto = Optional.empty();

    @Inject
    public void postConstruct() {
        photoCopyrightSetter = new PhotoCopyrightSetter();
    }
    
    public List<EditablePhoto> createPhotos(Path dir) {
        this.photos = photoApi.createPhotos(dir, new EditablePhotoFactory());
        photos.forEach(photo -> photo.addPropertyChangeListener(photoCopyrightSetter));
        return Collections.unmodifiableList(photos);
    }

    public List<EditablePhoto> validatePhotos() throws PhotoNotReadyException {
        List<EditablePhoto> notReadyPhotos = photos.stream()
        		.filter(photo -> !photo.isReadyToUpload())
        		.collect(Collectors.toList());
        if (photos.isEmpty() || !notReadyPhotos.isEmpty()) {
            throw new PhotoNotReadyException(notReadyPhotos);
        }
        return photos;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (!currentPhoto.isPresent() || !PhotoProperty.has(e.getPropertyName())) {
            return;
        }
       	photoApi.updateProperty(currentPhoto.get(), e.getPropertyName(), e.getNewValue());
    }

    @Override
    public void photoSelected(Optional<EditablePhoto> photo) {
        this.currentPhoto = photo;
    }

    private class PhotoCopyrightSetter implements PropertyChangeListener {

        private final Optional<String> creator;

        public PhotoCopyrightSetter() {
            creator = configReader.getProperty("copyright");
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(METADATA_PROPERTY)) {
                LocalPhoto photo = (LocalPhoto) e.getSource();
                photo.removePropertyChangeListener(this);
                creator.ifPresent(c -> photoApi.updateProperty(photo, "creator", c));
            }
        }

    }

}
