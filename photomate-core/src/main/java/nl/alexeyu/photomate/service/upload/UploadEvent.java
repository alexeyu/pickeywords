package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public abstract class UploadEvent {

    private final PhotoToStock photoToStock;

    public UploadEvent(PhotoToStock photoToStock) {
        this.photoToStock = photoToStock;
    }

    public EditablePhoto getPhoto() {
        return photoToStock.getPhoto();
    }

    public PhotoStock getPhotoStock() {
        return photoToStock.getPhotoStock();
    }

}
