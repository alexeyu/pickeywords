package nl.alexeyu.photomate.service.upload;

public class StateAwareUploadTask implements Runnable {

	private final Runnable task;
	
	private final UploadNotifier notifier;
	
	private final UploadAttempt uploadAttempt;
	
	public StateAwareUploadTask(UploadNotifier notifier, UploadAttempt uploadAttempt, Runnable task) {
		this.notifier = notifier;
		this.uploadAttempt = uploadAttempt;
		this.task = task;
	}

	@Override
	public void run() {
		try {
			notifier.notifyProgress(uploadAttempt, 0);
			task.run();
			notifier.notifySuccess(uploadAttempt);
		} catch (UploadException ex) {
			notifier.notifyError(uploadAttempt, ex);
		}
	}

}
