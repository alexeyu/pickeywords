package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.CAPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.DESCRIPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.KEYWORDS_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.service.upload.PhotoUploader;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.ImageUtils;

public class LocalPhotoManager implements PropertyChangeListener {

    private List<LocalPhoto> photos = new ArrayList<>();

    @Inject
    private LocalPhotoApi localPhotoApi;

    @Inject
    private ConfigReader configReader;

    @Inject
    private PhotoFactory photoFactory;
    
    @Inject
    private PhotoUploader photoUploader;

    private PhotoCopyrightSetter photoCopyrightSetter;
    
    private LocalPhoto photo;

    @Inject
    public void postConstruct() {
        photoCopyrightSetter = new PhotoCopyrightSetter();
    }
    
    public List<LocalPhoto> getPhotos() {
        return photos;
    }

    public void setPhotoFiles(File[] files) {
        photos = new ArrayList<>();
        for (File file : files) {
            if (ImageUtils.isJpeg(file)) {
                LocalPhoto photo = photoFactory.createLocalPhoto(file, localPhotoApi);
                photo.addPropertyChangeListener(photoCopyrightSetter);
                photos.add(photo);
            }
        }

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
        for (LocalPhoto photo : photos) {
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
