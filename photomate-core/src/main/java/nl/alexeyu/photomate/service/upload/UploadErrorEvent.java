package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadErrorEvent extends UploadEvent {

    private final Exception exception;

    private final int attemptsLeft;

    public UploadErrorEvent(EditablePhoto photo, PhotoStock photoStock, Exception ex, int attemptsLeft) {
        super(photo, photoStock);
        this.exception = ex;
        this.attemptsLeft = attemptsLeft;
    }

    public Exception getException() {
        return exception;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

}
