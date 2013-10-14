package nl.alexeyu.photomate.service;

import java.util.Collection;
import java.util.Random;

import nl.alexeyu.photomate.model.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class FakeUploadTask extends AbstractUploadTask {
	
	private static final double ERROR_PROBABILITY = 0.5;

	public FakeUploadTask(PhotoStock photoStock, LocalPhoto photo, int attemptsLeft,
			Collection<UploadPhotoListener> uploadPhotoListeners) {
		super(photoStock, photo, attemptsLeft, uploadPhotoListeners);
	}

	public void run() {
		notifyProgress(0);
		boolean error = new Random().nextDouble() < ERROR_PROBABILITY;
		if (error) {
			pause(1000);
			notifyError(new Exception());
		} else {
			for (int i = 1; i <= 10; i++) {
				notifyProgress(photo.getFile().length() / 10 * i);
				pause(100);
			}
			notifySuccess();
		}
	}

	private void pause(int msec) {
		try {
			Thread.sleep(msec);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
