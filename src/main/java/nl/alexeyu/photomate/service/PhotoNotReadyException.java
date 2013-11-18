package nl.alexeyu.photomate.service;

import java.util.List;

import nl.alexeyu.photomate.api.EditablePhoto;

public class PhotoNotReadyException extends Exception {
    
    private final List<EditablePhoto> photos;
    
    public PhotoNotReadyException(List<EditablePhoto> photos) {
        super("Photos are not ready to upload");
        this.photos = photos;
    }

    public List<EditablePhoto> getPhotos() {
        return photos;
    }

}
