package nl.alexeyu.photomate.service.upload;

import com.google.common.eventbus.EventBus;

public class EventBusUploadNotifier implements UploadNotifier {
	
	private final EventBus eventBus;
	
	public EventBusUploadNotifier(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void notifyProgress(UploadAttempt uploadAttempt, long bytes) {
		eventBus.post(new UploadProgressEvent(uploadAttempt, bytes));
	}

	@Override
	public void notifyError(UploadAttempt uploadAttempt, Exception ex) {
		eventBus.post(new UploadErrorEvent(uploadAttempt, ex));
	}

	@Override
	public void notifySuccess(UploadAttempt uploadAttempt) {
		eventBus.post(new UploadSuccessEvent(uploadAttempt));
	}

}
