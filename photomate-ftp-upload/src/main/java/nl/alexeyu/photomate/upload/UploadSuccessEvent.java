package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class UploadSuccessEvent extends UploadEvent {

    public UploadSuccessEvent(EditablePhoto photo, FtpEndpoint endpoint) {
        super(photo, endpoint);
    }

}
