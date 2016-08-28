package nl.alexeyu.photomate.service.upload;

import javax.inject.Inject;

import nl.alexeyu.photomate.util.ConfigReader;

public class UploadTaskFactory {

    @Inject
    private ConfigReader configReader;

	public Runnable create(UploadAttempt uploadAttempt, UploadNotifier notifier) {
		String realUploadProperty = configReader.getProperty("realUpload").orElse("true");
		return Boolean.valueOf(realUploadProperty)
				? new FtpUploadTask(uploadAttempt, notifier)
				: new FakeUploadTask(uploadAttempt, notifier);
	}

}
