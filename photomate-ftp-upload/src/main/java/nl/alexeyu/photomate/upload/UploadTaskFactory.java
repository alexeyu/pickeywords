package nl.alexeyu.photomate.upload;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;
import nl.alexeyu.photomate.util.Configuration;

public class UploadTaskFactory {

    @Inject
    private Configuration configuration;

    public Runnable create(EditablePhoto photo, PhotoStockAccess endpoint, UploadNotifier notifier) {
        String realUploadProperty = configuration.getProperty("realUpload").orElse("true");
        return Boolean.valueOf(realUploadProperty) 
                ? new FtpUploadTask(photo, endpoint, notifier)
                : new FakeUploadTask(photo, endpoint, notifier);
    }

}
