package nl.alexeyu.photomate.upload;

import java.util.Random;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class FakeUploadTask implements Runnable {

    private static final double ERROR_PROBABILITY = 0.5;

    private final EditablePhoto photo;

    private final FtpEndpoint endpoint;

    private final UploadNotifier notifier;

    public FakeUploadTask(EditablePhoto photo, FtpEndpoint endpoint, UploadNotifier notifier) {
        this.photo = photo;
        this.endpoint = endpoint;
        this.notifier = notifier;
    }

    public void run() {
        boolean error = new Random().nextDouble() < ERROR_PROBABILITY;
        if (error) {
            pause(1000);
            throw new UploadException();
        } else {
            for (int i = 1; i <= 10; i++) {
                notifier.notifyProgress(photo, endpoint, photo.fileSize() / 10 * i);
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
