package nl.alexeyu.photomate.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.CAPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.DESCRIPTION_PROPERTY;
import static nl.alexeyu.photomate.model.PhotoMetaData.KEYWORDS_PROPERTY;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.util.ConfigReader;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private List<EditablePhoto> photos = new ArrayList<>();

    @Inject
    private PhotoFactory photoFactory;

    @Inject
    private LocalPhotoApi localPhotoApi;

    @Inject
    private ConfigReader configReader;

    private PhotoCopyrightSetter photoCopyrightSetter;
    
    private LocalPhoto currentPhoto;

    @Inject
    public void postConstruct() {
        photoCopyrightSetter = new PhotoCopyrightSetter();
    }
    
    public List<EditablePhoto> createPhotos(File dir) {
        this.photos = photoFactory.createLocalPhotos(dir, localPhotoApi, EditablePhoto.class);
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
        if (currentPhoto == null) {
            return;
        }
        switch (e.getPropertyName()) {
        case CAPTION_PROPERTY:
            localPhotoApi.updateCaption(currentPhoto, e.getNewValue().toString());
            break;
        case DESCRIPTION_PROPERTY:
            localPhotoApi.updateDescription(currentPhoto, e.getNewValue().toString());
            break;
        case KEYWORDS_PROPERTY:
            localPhotoApi.updateKeywords(currentPhoto, (List<String>) e.getNewValue());
            break;
        }
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
                    localPhotoApi.updateCreator(photo, creator);
                }
            }
        }

    }

}
