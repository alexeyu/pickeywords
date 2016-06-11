package nl.alexeyu.photomate.service.upload;

import com.google.common.eventbus.EventBus;

public abstract class AbstractUploadTask implements Runnable {

    protected final PhotoToStock photoToStock;
    private final int attemptsLeft;

    private EventBus eventBus;

    public AbstractUploadTask(PhotoToStock photoToStock, int attemptsLeft) {
        this.photoToStock = photoToStock;
        this.attemptsLeft = attemptsLeft;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected final void notifyProgress(long bytes) {
        eventBus.post(new UploadProgressEvent(photoToStock, bytes));
    }

    protected final void notifyError(Exception ex) {
        eventBus.post(new UploadErrorEvent(photoToStock, ex, attemptsLeft));
    }

    protected final void notifySuccess() {
        eventBus.post(new UploadSuccessEvent(photoToStock));
    }

}