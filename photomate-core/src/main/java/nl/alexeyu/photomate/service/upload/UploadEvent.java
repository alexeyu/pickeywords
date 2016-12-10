package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public abstract class UploadEvent {

    protected final UploadAttempt uploadAttempt;

    public UploadEvent(UploadAttempt uploadAttempt) {
        this.uploadAttempt = uploadAttempt;
    }

    public EditablePhoto getPhoto() {
        return uploadAttempt.getPhoto();
    }

    public FtpEndpoint getEndpoint() {
        return uploadAttempt.getEndpoint();
    }

}
