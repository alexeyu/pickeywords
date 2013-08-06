package nl.alexeyu.photomate.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.util.ConfigReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PhotoUploader implements UploadPhotoListener {
	
	private static final int ATTEMPTS = 2;
	
	private UploadPhotoListener uiListener;

	private final Map<Photo, AtomicInteger> stocksToGo = new HashMap<>();
	
	private ConfigReader configReader;
	
	private ExecutorService heavyTaskExecutor;
	
	private ExecutorService lightTaskExecutor;
	
	public void uploadPhotos(List<PhotoStock> photoStocks, List<Photo> photos) {
		for (Photo photo : photos) {
			stocksToGo.put(photo, new AtomicInteger(photoStocks.size()));
			for (PhotoStock photoStock : photoStocks) {
				uploadPhoto(photoStock, photo, ATTEMPTS);
			}
		}
	}

	private void uploadPhoto(PhotoStock photoStock, Photo photo, int attemptsLeft) {
		UploadTask uploadTask = new FakeUploadTask(photoStock, photo, attemptsLeft, this, uiListener);
		heavyTaskExecutor.execute(uploadTask);
	}

	@Override
	public void onProgress(PhotoStock photoStock, Photo photo, long bytesUploaded) {
	}

	@Override
	public void onSuccess(PhotoStock photoStock, Photo photo) {
		AtomicInteger stocksCount = stocksToGo.get(photo);
		if (stocksCount.decrementAndGet() == 0) {
			String doneDir = configReader.getProperty("doneFolder", System.getProperty("java.io.tmpdir"));
			Runnable movePhotoTask = new MovePhotoTask(photo, new File(doneDir));
			lightTaskExecutor.execute(movePhotoTask);
		}
	}

	@Override
	public void onError(PhotoStock photoStock, Photo photo, Exception ex, int attemptsLeft) {
		if (attemptsLeft > 0) {
			uploadPhoto(photoStock, photo, attemptsLeft - 1);
		}
	}

	@Autowired
	public void setConfigReader(ConfigReader configReader) {
		this.configReader = configReader;
	}

	@Autowired
	public void setHeavyTaskExecutor(@Qualifier("heavyTaskExecutor") ExecutorService heavyTaskExecutor) {
		this.heavyTaskExecutor = heavyTaskExecutor;
	}

	@Autowired
	public void setLightTaskExecutor(@Qualifier("lightTaskExecutor") ExecutorService lightTaskExecutor) {
		this.lightTaskExecutor = lightTaskExecutor;
	}

	@Autowired
	public void setUiListener(@Qualifier("uploadTable") UploadPhotoListener uiListener) {
		this.uiListener = uiListener;
	}

}
