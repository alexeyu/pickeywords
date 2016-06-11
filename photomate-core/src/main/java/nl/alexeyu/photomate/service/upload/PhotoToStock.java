package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class PhotoToStock {

    private final EditablePhoto photo;
    
    private final PhotoStock photoStock;

    public PhotoToStock(EditablePhoto photo, PhotoStock photoStock) {
        this.photo = photo;
        this.photoStock = photoStock;
    }

    public EditablePhoto getPhoto() {
        return photo;
    }

    public PhotoStock getPhotoStock() {
        return photoStock;
    }

}
