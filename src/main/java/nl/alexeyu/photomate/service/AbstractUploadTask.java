package nl.alexeyu.photomate.service;

import java.util.Collection;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

public abstract class AbstractUploadTask implements WeighedTask {

	protected final PhotoStock photoStock;
	protected final Photo photo;
	protected final int attemptsLeft;
	protected final Collection<UploadPhotoListener> uploadPhotoListeners;

	public AbstractUploadTask(PhotoStock photoStock, Photo photo, int attemptsLeft, 
			Collection<UploadPhotoListener> uploadPhotoListeners) {
		this.photoStock = photoStock;
		this.photo = photo;
		this.attemptsLeft = attemptsLeft;
		this.uploadPhotoListeners = uploadPhotoListeners;
	}

	@Override
	public TaskWeight getWeight() {
		return TaskWeight.HEAVY;
	}

	protected final void notifyProgress(long bytes) {
		for (UploadPhotoListener l : uploadPhotoListeners) {
			l.onProgress(photoStock, photo, bytes);
		}
	}

	protected final void notifyError(Exception ex) {
		for (UploadPhotoListener l : uploadPhotoListeners) {
			l.onError(photoStock, photo, ex, attemptsLeft);
		}
	}
	
	protected final void notifySuccess() {
		for (UploadPhotoListener l : uploadPhotoListeners) {
			l.onSuccess(photoStock, photo);
		}
	}

}