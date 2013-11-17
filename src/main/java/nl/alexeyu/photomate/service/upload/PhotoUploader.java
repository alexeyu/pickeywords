package nl.alexeyu.photomate.service.upload;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.ArchivePhotoTask;
import nl.alexeyu.photomate.util.ConfigReader;

public class PhotoUploader implements UploadPhotoListener {
	
	private static final int ATTEMPTS = 2;
	
	private ConcurrentHashMap<EditablePhoto, Boolean> archivedPhotos;
	
	private String archiveDir;
	
	@Inject
	private ConfigReader configReader;
	
	@Inject
	private ExecutorService taskExecutor;
	
	private Collection<UploadPhotoListener> listeners; 
	
	public void uploadPhotos(List<EditablePhoto> photos) {
	    archivedPhotos = new ConcurrentHashMap<>();
        archiveDir = configReader.getProperty("archiveFolder", null);
		List<PhotoStock> photoStocks = configReader.getPhotoStocks();
		for (EditablePhoto photo : photos) {
			for (PhotoStock photoStock : photoStocks) {
				uploadPhoto(photoStock, photo, ATTEMPTS);
			}
		}
	}

	private void uploadPhoto(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft) {
		Runnable uploadTask;
		if (Boolean.valueOf(configReader.getProperty("realUpload", "true"))) {
		    uploadTask = new FtpUploadTask(photoStock, photo, attemptsLeft, listeners);
		} else {
            uploadTask = new FakeUploadTask(photoStock, photo, attemptsLeft, listeners);
		}
		taskExecutor.execute(uploadTask);
	}

	@Override
	public void onProgress(PhotoStock photoStock, EditablePhoto photo, long bytesUploaded) {
	}

	@Override
	public void onSuccess(PhotoStock photoStock, EditablePhoto photo) {
		if (archivedPhotos.put(photo, Boolean.TRUE) == null) {
			if (archiveDir != null) {
			    Runnable archivePhotoTask = new ArchivePhotoTask(photo, new File(archiveDir));
			    taskExecutor.execute(archivePhotoTask);
			}
		}
	}

	@Override
	public void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft) {
		if (attemptsLeft > 0) {
			uploadPhoto(photoStock, photo, attemptsLeft - 1);
		}
	}

	@Inject
	public void setUploadPhotoListener(UploadPhotoListener listener) {
		listeners = Arrays.asList(listener, this);
	}

}
