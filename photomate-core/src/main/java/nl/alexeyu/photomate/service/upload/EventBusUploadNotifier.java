package nl.alexeyu.photomate.service.upload;

import com.google.common.eventbus.EventBus;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class EventBusUploadNotifier implements UploadNotifier {

    private final EventBus eventBus;

    public EventBusUploadNotifier(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void notifyProgress(EditablePhoto photo, FtpEndpoint endpoint, long bytes) {
        eventBus.post(new UploadProgressEvent(photo, endpoint, bytes));
    }

    @Override
    public void notifyError(EditablePhoto photo, FtpEndpoint endpoint, Exception ex) {
        eventBus.post(new UploadErrorEvent(photo, endpoint, ex));
    }

    @Override
    public void notifySuccess(EditablePhoto photo, FtpEndpoint endpoint) {
        eventBus.post(new UploadSuccessEvent(photo, endpoint));
    }

}
