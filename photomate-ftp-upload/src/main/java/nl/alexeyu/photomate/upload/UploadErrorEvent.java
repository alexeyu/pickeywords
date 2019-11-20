package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;

public class UploadErrorEvent extends UploadEvent {

    private final Exception exception;

    public UploadErrorEvent(EditablePhoto photo, PhotoStockAccess endpoint, Exception ex) {
        super(photo, endpoint);
        this.exception = ex;
    }

    public Exception getException() {
        return exception;
    }

}
