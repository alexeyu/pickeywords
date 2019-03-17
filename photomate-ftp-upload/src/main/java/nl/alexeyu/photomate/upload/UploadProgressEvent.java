package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class UploadProgressEvent extends UploadEvent {

    private final long bytesUploaded;

    public UploadProgressEvent(EditablePhoto photo, FtpEndpoint endpoint, long bytesUploaded) {
        super(photo, endpoint);
        this.bytesUploaded = bytesUploaded;
    }

    public long getBytesUploaded() {
        return bytesUploaded;
    }

}
