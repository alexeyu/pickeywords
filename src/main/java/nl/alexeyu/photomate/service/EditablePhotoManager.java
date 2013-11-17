package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.CAPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.DESCRIPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.KEYWORDS_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.service.upload.PhotoUploader;
import nl.alexeyu.photomate.ui.PhotoObserver;
import nl.alexeyu.photomate.util.ConfigReader;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private List<EditablePhoto> photos = new ArrayList<>();

    @Inject
    private PhotoFactory photoFactory;

    @Inject
    private LocalPhotoApi localPhotoApi;

    @Inject
    private ConfigReader configReader;

    @Inject
    private PhotoUploader photoUploader;

    private PhotoCopyrightSetter photoCopyrightSetter;
    
    private LocalPhoto photo;

    @Inject
    public void postConstruct() {
        photoCopyrightSetter = new PhotoCopyrightSetter();
    }
    
    public List<EditablePhoto> getPhotos() {
        return photos;
    }

    public List<EditablePhoto> createPhotos(File dir) {
        this.photos = photoFactory.createLocalPhotos(dir, localPhotoApi, EditablePhoto.class);
        for (LocalPhoto photo : photos) {
            photo.addPropertyChangeListener(photoCopyrightSetter);
        }
        return Collections.unmodifiableList(photos);
    }

    public void uploadPhotos() {
        if (validatePhotos()) {
            photoUploader.uploadPhotos(photos);
        }
    }
    
    public boolean validatePhotos() {
        if (photos.size() == 0) {
            return false;
        }
        for (EditablePhoto photo : photos) {
            if (!photo.isReadyToUpload()) {
                return false;
            }
        }
        return true;
    }
    
    public void setCurrentPhoto(LocalPhoto photo) {
        this.photo = photo;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (photo == null) {
            return;
        }
        switch (e.getPropertyName()) {
        case CAPTION_PROPERTY:
            localPhotoApi.updateCaption(photo, e.getNewValue().toString());
            break;
        case DESCRIPTION_PROPERTY:
            localPhotoApi.updateDescription(photo, e.getNewValue().toString());
            break;
        case KEYWORDS_PROPERTY:
            localPhotoApi.updateKeywords(photo, (List<String>) e.getNewValue());
            break;
        }
    }

    @Override
    public void photoSelected(EditablePhoto photo) {
        this.photo = photo;
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
                    localPhotoApi.updateCreator(photo, creator);
                }
            }
        }

    }

}
