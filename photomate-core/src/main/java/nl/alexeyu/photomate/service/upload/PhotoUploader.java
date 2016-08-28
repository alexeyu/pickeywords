package nl.alexeyu.photomate.service.upload;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.util.ConfigReader;

public final class PhotoUploader {

    private static final int DEFAULT_UPLOAD_ATTEMPTS = 2;

    private final ConfigReader configReader;

    private final EventBus eventBus;
    
    private final UploadTaskFactory taskFactory;

    @Inject
    public PhotoUploader(ConfigReader configReader, EventBus eventBus, UploadTaskFactory taskFactory) {
		this.configReader = configReader;
		this.eventBus = eventBus;
		this.taskFactory = taskFactory;
	}

	@Inject
    public void init() {
        eventBus.register(this);
    }

    public void uploadPhotos(List<EditablePhoto> photos) {
        List<PhotoStock> photoStocks = configReader.getPhotoStocks();
        int initialAttemptLeft = Integer.valueOf(configReader.getProperty("uploadAttempts").orElse("" + DEFAULT_UPLOAD_ATTEMPTS));
        photos.forEach(photo -> 
        	photoStocks.forEach(photoStock -> 
        		uploadPhoto(new UploadAttempt(photo, photoStock, initialAttemptLeft))));
    }

    private void uploadPhoto(UploadAttempt uploadAttempt) {
        UploadNotifier notifier = new EventBusUploadNotifier(eventBus);
        Runnable uploadTask = taskFactory.create(uploadAttempt, notifier);
        Runnable notifyingUploadTask = new StateAwareUploadTask(notifier, uploadAttempt, uploadTask);
        CompletableFuture.runAsync(notifyingUploadTask);
    }

    @Subscribe
    public void onError(UploadErrorEvent event) {
    	Optional<UploadAttempt> nextAttempt = event.nextAttempt();
        if (nextAttempt.isPresent()) {
            uploadPhoto(nextAttempt.get());
        }
    }

}
