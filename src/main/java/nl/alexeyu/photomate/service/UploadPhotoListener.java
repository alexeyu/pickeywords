package nl.alexeyu.photomate.service;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

public interface UploadPhotoListener {
	
	void onProgress(PhotoStock photoStock, Photo photo, long bytesUploaded);

	void onError(PhotoStock photoStock, Photo photo, Exception ex, int attemptsLeft);

	void onSuccess(PhotoStock photoStock, Photo photo);
	
}
