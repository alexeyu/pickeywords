package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadSuccessEvent extends UploadEvent {

    public UploadSuccessEvent(EditablePhoto photo, PhotoStock photoStock) {
        super(photo, photoStock);
    }

}
