package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public interface UploadPhotoListener {
	
	void onProgress(PhotoStock photoStock, EditablePhoto photo, long bytesUploaded);

	void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft);

	void onSuccess(PhotoStock photoStock, EditablePhoto photo);
	
}
