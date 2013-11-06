package nl.alexeyu.photomate.service.upload;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.MovePhotoTask;
import nl.alexeyu.photomate.util.ConfigReader;

public class PhotoUploader implements UploadPhotoListener {
	
	private static final int ATTEMPTS = 2;
	
	private Map<LocalPhoto, AtomicInteger> stocksToGo;
	
	@Inject
	private ConfigReader configReader;
	
	@Inject
	private ExecutorService taskExecutor;
	
	private Collection<UploadPhotoListener> listeners; 
	
	public void uploadPhotos(List<LocalPhoto> photos) {
		stocksToGo = new ConcurrentHashMap<>();
		List<PhotoStock> photoStocks = configReader.getPhotoStocks();
		for (LocalPhoto photo : photos) {
			stocksToGo.put(photo, new AtomicInteger(photoStocks.size()));
			for (PhotoStock photoStock : photoStocks) {
				uploadPhoto(photoStock, photo, ATTEMPTS);
			}
		}
	}

	private void uploadPhoto(PhotoStock photoStock, LocalPhoto photo, int attemptsLeft) {
		Runnable uploadTask = new FakeUploadTask(photoStock, photo, attemptsLeft, listeners);
		taskExecutor.execute(uploadTask);
	}

	@Override
	public void onProgress(PhotoStock photoStock, LocalPhoto photo, long bytesUploaded) {
	}

	@Override
	public void onSuccess(PhotoStock photoStock, LocalPhoto photo) {
		AtomicInteger stocksCount = stocksToGo.get(photo);
		if (stocksCount.decrementAndGet() == 0) {
			String doneDir = configReader.getProperty("doneFolder", System.getProperty("java.io.tmpdir"));
			Runnable movePhotoTask = new MovePhotoTask(photo, new File(doneDir));
			taskExecutor.execute(movePhotoTask);
		}
	}

	@Override
	public void onError(PhotoStock photoStock, LocalPhoto photo, Exception ex, int attemptsLeft) {
		if (attemptsLeft > 0) {
			uploadPhoto(photoStock, photo, attemptsLeft - 1);
		}
	}

	@Inject
	public void setUploadPhotoListener(UploadPhotoListener listener) {
		listeners = Arrays.asList(listener, this);
	}

}