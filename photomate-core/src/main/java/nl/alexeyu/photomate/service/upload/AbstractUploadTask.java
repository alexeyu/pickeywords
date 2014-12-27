package nl.alexeyu.photomate.service.upload;

import java.util.Collection;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public abstract class AbstractUploadTask implements Runnable {

	protected final PhotoStock photoStock;
	protected final EditablePhoto photo;
	private final int attemptsLeft;
	private final Collection<UploadPhotoListener> uploadPhotoListeners;

	public AbstractUploadTask(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft, 
			Collection<UploadPhotoListener> uploadPhotoListeners) {
		this.photoStock = photoStock;
		this.photo = photo;
		this.attemptsLeft = attemptsLeft;
		this.uploadPhotoListeners = uploadPhotoListeners;
	}

	protected final void notifyProgress(long bytes) {
		uploadPhotoListeners.forEach(listener -> listener.onProgress(photoStock, photo, bytes));
	}

	protected final void notifyError(Exception ex) {
		uploadPhotoListeners.forEach(listener -> listener.onError(photoStock, photo, ex, attemptsLeft));
	}
	
	protected final void notifySuccess() {
		uploadPhotoListeners.forEach(listener -> listener.onSuccess(photoStock, photo));
	}

   protected final void pause(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception ignored) {}
    }
   
}