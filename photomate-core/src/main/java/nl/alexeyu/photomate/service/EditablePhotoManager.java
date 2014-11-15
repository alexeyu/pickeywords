package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.api.editable.EditablePhotoApi;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.ConfigReader;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private List<EditablePhoto> photos = new ArrayList<>();

    @Inject
    private EditablePhotoApi photoApi;

    @Inject
    private ConfigReader configReader;

    private PhotoCopyrightSetter photoCopyrightSetter;
    
    private LocalPhoto currentPhoto;

    @Inject
    public void postConstruct() {
        photoCopyrightSetter = new PhotoCopyrightSetter();
    }
    
    public List<EditablePhoto> createPhotos(Path dir) {
        this.photos = photoApi.createPhotos(dir);
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
        if (currentPhoto == null || !PhotoProperty.has(e.getPropertyName())) {
            return;
        }
       	photoApi.updateProperty(currentPhoto, e.getPropertyName(), e.getNewValue());
    }

    @Override
    public void photoSelected(EditablePhoto photo) {
        this.currentPhoto = photo;
    }

    private class PhotoCopyrightSetter implements PropertyChangeListener {

        private final String creator;

        public PhotoCopyrightSetter() {
            creator = configReader.getProperty("copyright", null);
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(METADATA_PROPERTY)) {
                LocalPhoto photo = (LocalPhoto) e.getSource();
                photo.removePropertyChangeListener(this);
                if (creator != null) {
                    photoApi.updateProperty(photo, "creator", creator);
                }
            }
        }

    }

}
