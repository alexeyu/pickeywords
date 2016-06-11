package nl.alexeyu.photomate.service.upload;

public class UploadProgressEvent extends UploadEvent {

    private final long bytesUploaded;

    public UploadProgressEvent(PhotoToStock photoToStock, long bytesUploaded) {
        super(photoToStock);
        this.bytesUploaded = bytesUploaded;
    }

    public long getBytesUploaded() {
        return bytesUploaded;
    }

}
