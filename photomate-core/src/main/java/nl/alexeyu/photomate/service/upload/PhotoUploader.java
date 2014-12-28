package nl.alexeyu.photomate.service.upload;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.util.ConfigReader;

import com.google.common.collect.Lists;

public class PhotoUploader implements UploadPhotoListener {
	
	private static final int ATTEMPTS = 2;
	
	@Inject
	private ConfigReader configReader;
	
	private Collection<UploadPhotoListener> listeners; 
	
	public void uploadPhotos(List<EditablePhoto> photos, UploadPhotoListener... listeners) {
	    this.listeners = Lists.asList(this, listeners);
		List<PhotoStock> photoStocks = configReader.getPhotoStocks();
		photos.forEach(photo ->
			photoStocks.forEach(photoStock -> 
				uploadPhoto(photoStock, photo, ATTEMPTS)));
	}
	
	private void uploadPhoto(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft) {
		String realUploadProperty = configReader.getProperty("realUpload").orElse("true");
		Runnable uploadTask = Boolean.valueOf(realUploadProperty)
		    ? new FtpUploadTask(photoStock, photo, attemptsLeft, listeners)
            : new FakeUploadTask(photoStock, photo, attemptsLeft, listeners);
		CompletableFuture.runAsync(uploadTask);
	}

	@Override
	public void onProgress(PhotoStock photoStock, EditablePhoto photo, long bytesUploaded) {}

	@Override
	public void onSuccess(PhotoStock photoStock, EditablePhoto photo) {}

	@Override
	public void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft) {
		if (attemptsLeft > 0) {
			uploadPhoto(photoStock, photo, attemptsLeft - 1);
		}
	}

}
