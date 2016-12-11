package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public abstract class UploadEvent {

    protected final EditablePhoto photo;
    
    protected final FtpEndpoint endpoint;

	public UploadEvent(EditablePhoto photo, FtpEndpoint endpoint) {
		this.photo = photo;
		this.endpoint = endpoint;
	}

	public EditablePhoto getPhoto() {
		return photo;
	}

	public FtpEndpoint getEndpoint() {
		return endpoint;
	}

}
