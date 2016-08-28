package nl.alexeyu.photomate.service.upload;

public class UploadSuccessEvent extends UploadEvent {

    public UploadSuccessEvent(UploadAttempt photoToStock) {
        super(photoToStock);
    }

}
