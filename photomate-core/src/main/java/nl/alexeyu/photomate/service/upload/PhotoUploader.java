package nl.alexeyu.photomate.service.upload;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.ArchivePhotoTask;
import nl.alexeyu.photomate.util.ConfigReader;

public class PhotoUploader implements UploadPhotoListener {
	
	private static final int ATTEMPTS = 2;
	
	private ConcurrentHashMap<EditablePhoto, Boolean> archivedPhotos = new ConcurrentHashMap<>();
	
	private Optional<String> archiveDir;
	
	@Inject
	private ConfigReader configReader;
	
	private Collection<UploadPhotoListener> listeners = new ArrayList<>(Arrays.asList( (UploadPhotoListener) this)); 
	
	public void uploadPhotos(List<EditablePhoto> photos, UploadPhotoListener l) {
	    listeners.add(l);
        archiveDir = configReader.getProperty("archiveFolder");
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
	public void onProgress(PhotoStock photoStock, EditablePhoto photo, long bytesUploaded) {
	}

	@Override
	public void onSuccess(PhotoStock photoStock, EditablePhoto photo) {
		if (archivedPhotos.put(photo, Boolean.TRUE) == null) {
			archiveDir.ifPresent(dir ->
			    CompletableFuture.runAsync(new ArchivePhotoTask(photo, Paths.get(dir)))
			);
		}
	}

	@Override
	public void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft) {
		if (attemptsLeft > 0) {
			uploadPhoto(photoStock, photo, attemptsLeft - 1);
		}
	}

}
