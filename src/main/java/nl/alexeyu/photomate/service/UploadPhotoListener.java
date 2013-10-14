package nl.alexeyu.photomate.service;

import nl.alexeyu.photomate.model.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public interface UploadPhotoListener {
	
	void onProgress(PhotoStock photoStock, LocalPhoto photo, long bytesUploaded);

	void onError(PhotoStock photoStock, LocalPhoto photo, Exception ex, int attemptsLeft);

	void onSuccess(PhotoStock photoStock, LocalPhoto photo);
	
}
