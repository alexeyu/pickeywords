package nl.alexeyu.photomate.service.upload;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public interface UploadPhotoListener {
	
	void onProgress(PhotoStock photoStock, LocalPhoto photo, long bytesUploaded);

	void onError(PhotoStock photoStock, LocalPhoto photo, Exception ex, int attemptsLeft);

	void onSuccess(PhotoStock photoStock, LocalPhoto photo);
	
}
