package nl.alexeyu.photomate.upload;

import com.google.common.eventbus.EventBus;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;

public class EventBusUploadNotifier implements UploadNotifier {

    private final EventBus eventBus;

    public EventBusUploadNotifier(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void notifyProgress(EditablePhoto photo, PhotoStockAccess endpoint, long bytes) {
        eventBus.post(new UploadProgressEvent(photo, endpoint, bytes));
    }

    @Override
    public void notifyError(EditablePhoto photo, PhotoStockAccess endpoint, Exception ex) {
        eventBus.post(new UploadErrorEvent(photo, endpoint, ex));
    }

    @Override
    public void notifySuccess(EditablePhoto photo, PhotoStockAccess endpoint) {
        eventBus.post(new UploadSuccessEvent(photo, endpoint));
    }

}
