package nl.alexeyu.photomate.service.upload;

import java.util.Optional;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadAttempt {

    private final EditablePhoto photo;
    
    private final PhotoStock photoStock;
    
    private final int attemptsLeft;

    public UploadAttempt(EditablePhoto photo, PhotoStock photoStock, int attemptsLeft) {
        this.photo = photo;
        this.photoStock = photoStock;
        this.attemptsLeft = attemptsLeft;
    }

    public EditablePhoto getPhoto() {
        return photo;
    }

    public PhotoStock getPhotoStock() {
        return photoStock;
    }
    
    public Optional<UploadAttempt> next() {
    	return attemptsLeft > 1 
    			? Optional.of(new UploadAttempt(photo, photoStock, attemptsLeft - 1)) 
    			: Optional.empty();
    }
}
