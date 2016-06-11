package nl.alexeyu.photomate.service.upload;

import java.util.Random;

public class FakeUploadTask extends AbstractUploadTask {

    private static final double ERROR_PROBABILITY = 0.5;

    public FakeUploadTask(PhotoToStock photoToStock, int attemptsLeft) {
        super(photoToStock, attemptsLeft);
    }

    public void run() {
        notifyProgress(0);
        boolean error = new Random().nextDouble() < ERROR_PROBABILITY;
        if (error) {
            pause(1000);
            notifyError(new Exception());
        } else {
            for (int i = 1; i <= 10; i++) {
                notifyProgress(photoToStock.getPhoto().fileSize() / 10 * i);
                pause(100);
            }
            notifySuccess();
        }
    }

    private final void pause(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception ignored) {
        }
    }

}
