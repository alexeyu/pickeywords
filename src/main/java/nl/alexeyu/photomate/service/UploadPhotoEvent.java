package nl.alexeyu.photomate.service;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadPhotoEvent {

	private final Photo photo;

	private final PhotoStock photoStock;

	private Exception exception;
	
	private long uploadedBytes;
	
	public UploadPhotoEvent(Photo photo, PhotoStock photoStock, long uploadedBytes) {
		this.photo = photo;
		this.photoStock = photoStock;
		this.uploadedBytes = uploadedBytes;
	}
	
	public UploadPhotoEvent(Photo photo, PhotoStock photoStock, Exception exception) {
		this.photo = photo;
		this.photoStock = photoStock;
		this.exception = exception;
	}

	public UploadPhotoStatus getStatus() {
		if (exception != null) {
			return UploadPhotoStatus.FAILURE;
		}
		if (uploadedBytes == 0) {
			return UploadPhotoStatus.QUEUED;
		}
		return uploadedBytes < photo.getFile().length() ? 
				UploadPhotoStatus.IN_PROGRESS : UploadPhotoStatus.SUCCSESS;
	}

	public Photo getPhoto() {
		return photo;
	}

	public PhotoStock getPhotoStock() {
		return photoStock;
	}

	public Exception getException() {
		return exception;
	}

	public int getPercentUploaded() {
		return (int) (uploadedBytes * 100 / photo.getFile().length());
	}

}
