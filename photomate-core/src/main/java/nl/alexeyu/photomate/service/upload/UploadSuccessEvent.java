package nl.alexeyu.photomate.service.upload;

public class UploadSuccessEvent extends UploadEvent {

    public UploadSuccessEvent(UploadAttempt attempt) {
        super(attempt);
    }

}
