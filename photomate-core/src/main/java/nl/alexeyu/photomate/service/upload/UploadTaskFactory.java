package nl.alexeyu.photomate.service.upload;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;
import nl.alexeyu.photomate.util.ConfigReader;

public class UploadTaskFactory {

    @Inject
    private ConfigReader configReader;

    public Runnable create(EditablePhoto photo, FtpEndpoint endpoint, UploadNotifier notifier) {
        String realUploadProperty = configReader.getProperty("realUpload").orElse("true");
        return Boolean.valueOf(realUploadProperty) 
                ? new FtpUploadTask(photo, endpoint, notifier)
                : new FakeUploadTask(photo, endpoint, notifier);
    }

}
