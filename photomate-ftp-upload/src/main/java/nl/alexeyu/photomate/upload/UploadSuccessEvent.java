package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;

public class UploadSuccessEvent extends UploadEvent {

    public UploadSuccessEvent(EditablePhoto photo, PhotoStockAccess endpoint) {
        super(photo, endpoint);
    }

}
