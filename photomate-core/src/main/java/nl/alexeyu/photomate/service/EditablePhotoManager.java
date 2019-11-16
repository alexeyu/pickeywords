package nl.alexeyu.photomate.service;

import static nl.alexeyu.photomate.api.AbstractPhoto.METADATA_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Strings;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.MediaFileProcessors;

public class EditablePhotoManager implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private List<EditablePhoto> photos = new ArrayList<>();

    private LocalPhotoApi<EditablePhoto> photoApi;

    private LocalPhotoApi<EditablePhoto> videoApi;

    private ConfigReader configReader;

    private EditablePhoto currentPhoto;

    @Inject
    public EditablePhotoManager(@Named("photoApi") LocalPhotoApi<EditablePhoto> photoApi,
                                @Named("videoApi") LocalPhotoApi<EditablePhoto> videoApi,
                                ConfigReader configReader) {
        this.photoApi = photoApi;
        this.videoApi = videoApi;
        this.configReader = configReader;
    }

    public List<EditablePhoto> createPhotos(Path dir) {
        var photoPaths = MediaFileProcessors.JPEG.apply(dir);
        var videoPaths = MediaFileProcessors.MPEG4.apply(dir);
        this.photos = photoApi.createPhotos(photoPaths, EditablePhoto::new);
        this.photos.addAll(videoApi.createPhotos(videoPaths, EditablePhoto::new));
        configReader.getProperty("copyright").map(PhotoCopyrightSetter::new)
                .ifPresent(creator -> photos.forEach(photo -> photo.addPropertyChangeListener(creator)));
        return photos;
    }

    public List<EditablePhoto> validatePhotos() throws PhotoNotReadyException {
        var notReadyPhotos = photos.stream().filter(photo -> !photo.isReadyToUpload()).collect(Collectors.toList());
        if (photos.isEmpty() || !notReadyPhotos.isEmpty()) {
            throw new PhotoNotReadyException(notReadyPhotos);
        }
        return photos;
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

    public void copyMetadata(LocalPhoto target, Photo source) {
        if (!target.keywords().isEmpty()) {
            var extendedKeywords = new LinkedHashSet<>(target.keywords());
            extendedKeywords.addAll(source.metaData().keywords());
            photoApi.updateProperty(target, PhotoProperty.KEYWORDS, extendedKeywords);
        }
        if (!Strings.isNullOrEmpty(source.metaData().caption())) {
            photoApi.updateProperty(target, PhotoProperty.CAPTION, source.metaData().caption());
        }
        if (!Strings.isNullOrEmpty(source.metaData().description())) {
            photoApi.updateProperty(target, PhotoProperty.DESCRIPTION, source.metaData().description());
        }
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
