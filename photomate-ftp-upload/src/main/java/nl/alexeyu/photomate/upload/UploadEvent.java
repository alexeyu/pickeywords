package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;

public abstract class UploadEvent {

    protected final EditablePhoto photo;

    protected final PhotoStockAccess endpoint;

    public UploadEvent(EditablePhoto photo, PhotoStockAccess endpoint) {
        this.photo = photo;
        this.endpoint = endpoint;
    }

    public EditablePhoto getPhoto() {
        return photo;
    }

    public PhotoStockAccess getEndpoint() {
        return endpoint;
    }

}
