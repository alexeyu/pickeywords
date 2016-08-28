package nl.alexeyu.photomate.service.upload;

import java.util.Random;

public class FakeUploadTask implements Runnable {

    private static final double ERROR_PROBABILITY = 0.5;
    
    private final UploadAttempt uploadAttempt;
    
    private final UploadNotifier notifier;
    
    public FakeUploadTask(UploadAttempt uploadAttempt, UploadNotifier notifier) {
    	this.uploadAttempt = uploadAttempt;
    	this.notifier = notifier;
    }

    public void run() {
        boolean error = new Random().nextDouble() < ERROR_PROBABILITY;
        if (error) {
            pause(1000);
            throw new UploadException();
        } else {
            for (int i = 1; i <= 10; i++) {
            	notifier.notifyProgress(uploadAttempt, uploadAttempt.getPhoto().fileSize() / 10 * i);
                pause(100);
            }
        }
    }

    private final void pause(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception ignored) {
        }
    }

}
