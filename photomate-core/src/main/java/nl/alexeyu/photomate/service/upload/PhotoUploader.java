package nl.alexeyu.photomate.service.upload;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.util.ConfigReader;

public class PhotoUploader {
	
	private static final int ATTEMPTS = 2;
	
	@Inject
	private ConfigReader configReader;
	
	@Inject
	private EventBus eventBus;
	
	@Inject
	public void init() {
		eventBus.register(this);
	}

	public void uploadPhotos(List<EditablePhoto> photos) {
		List<PhotoStock> photoStocks = configReader.getPhotoStocks();
		photos.forEach(photo ->
			photoStocks.forEach(photoStock -> 
				uploadPhoto(photoStock, photo, ATTEMPTS)));
	}
	
	private void uploadPhoto(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft) {
		String realUploadProperty = configReader.getProperty("realUpload").orElse("true");
		AbstractUploadTask uploadTask = Boolean.valueOf(realUploadProperty)
		    ? new FtpUploadTask(photoStock, photo, attemptsLeft)
            : new FakeUploadTask(photoStock, photo, attemptsLeft);
		uploadTask.setEventBus(eventBus);
		CompletableFuture.runAsync(uploadTask);
	}

	@Subscribe
	public void onError(UploadErrorEvent ev) {
		if (ev.getAttemptsLeft() > 0) {
			uploadPhoto(ev.getPhotoStock(), ev.getPhoto(), ev.getAttemptsLeft() - 1);
		}
	}

}
