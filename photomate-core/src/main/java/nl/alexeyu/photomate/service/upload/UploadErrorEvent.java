package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class UploadErrorEvent extends UploadEvent {

    private final Exception exception;

    public UploadErrorEvent(EditablePhoto photo, FtpEndpoint endpoint, Exception ex) {
        super(photo, endpoint);
        this.exception = ex;
    }

    public Exception getException() {
        return exception;
    }

}
