package nl.alexeyu.photomate.service.upload;

import java.util.Optional;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class UploadAttempt {

    private final EditablePhoto photo;
    
    private final FtpEndpoint endpoint;
    
    private final int attemptsLeft;

    public UploadAttempt(EditablePhoto photo, FtpEndpoint endpoint, int attemptsLeft) {
        this.photo = photo;
        this.endpoint = endpoint;
        this.attemptsLeft = attemptsLeft;
    }

    public EditablePhoto getPhoto() {
        return photo;
    }

    public FtpEndpoint getEndpoint() {
        return endpoint;
    }
    
    public Optional<UploadAttempt> next() {
    	return attemptsLeft > 1 
    			? Optional.of(new UploadAttempt(photo, endpoint, attemptsLeft - 1)) 
    			: Optional.empty();
    }
}
