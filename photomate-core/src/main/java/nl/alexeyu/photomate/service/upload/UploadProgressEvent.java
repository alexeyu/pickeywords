package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadProgressEvent extends UploadEvent {

    private final long bytesUploaded;

    public UploadProgressEvent(EditablePhoto photo, PhotoStock photoStock, long bytesUploaded) {
        super(photo, photoStock);
        this.bytesUploaded = bytesUploaded;
    }

    public long getBytesUploaded() {
        return bytesUploaded;
    }

}
