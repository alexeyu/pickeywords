package nl.alexeyu.photomate.service.upload;

public class UploadErrorEvent extends UploadEvent {

    private final Exception exception;

    private final int attemptsLeft;

    public UploadErrorEvent(PhotoToStock photoToStock, Exception ex, int attemptsLeft) {
        super(photoToStock);
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
