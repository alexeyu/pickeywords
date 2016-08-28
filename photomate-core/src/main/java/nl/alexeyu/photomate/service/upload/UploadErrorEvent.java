package nl.alexeyu.photomate.service.upload;

import java.util.Optional;

public class UploadErrorEvent extends UploadEvent {

    private final Exception exception;

    public UploadErrorEvent(UploadAttempt uploadAttempt, Exception ex) {
        super(uploadAttempt);
        this.exception = ex;
    }

    public Exception getException() {
        return exception;
    }

    public Optional<UploadAttempt> nextAttempt() {
        return uploadAttempt.next();
    }

}
