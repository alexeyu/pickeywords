package nl.alexeyu.photomate.service.upload;

import com.google.common.eventbus.EventBus;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public abstract class AbstractUploadTask implements Runnable {

    protected final PhotoStock photoStock;
    protected final EditablePhoto photo;
    private final int attemptsLeft;

    private EventBus eventBus;

    public AbstractUploadTask(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft) {
        this.photoStock = photoStock;
        this.photo = photo;
        this.attemptsLeft = attemptsLeft;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected final void notifyProgress(long bytes) {
        eventBus.post(new UploadProgressEvent(photo, photoStock, bytes));
    }

    protected final void notifyError(Exception ex) {
        eventBus.post(new UploadErrorEvent(photo, photoStock, ex, attemptsLeft));
    }

    protected final void notifySuccess() {
        eventBus.post(new UploadSuccessEvent(photo, photoStock));
    }

    protected final void pause(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception ignored) {
        }
    }

}