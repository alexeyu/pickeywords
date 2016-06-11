package nl.alexeyu.photomate.service.upload;

public class UploadSuccessEvent extends UploadEvent {

    public UploadSuccessEvent(PhotoToStock photoToStock) {
        super(photoToStock);
    }

}
