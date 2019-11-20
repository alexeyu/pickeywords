package nl.alexeyu.photomate.upload;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.util.Configuration;
import reactor.core.publisher.Mono;

public final class PhotoUploader {

    private static final int DEFAULT_UPLOAD_ATTEMPTS = 2;

    private final Configuration configuration;

    private final UploadNotifier notifier;

    private final UploadTaskFactory taskFactory;

    private final int attempts;

    @Inject
    public PhotoUploader(Configuration configuration, EventBus eventBus, UploadTaskFactory taskFactory) {
        this.configuration = configuration;
        this.notifier = new EventBusUploadNotifier(eventBus);
        this.taskFactory = taskFactory;
        this.attempts = Integer
                .valueOf(configuration.getProperty("uploadAttempts").orElse("" + DEFAULT_UPLOAD_ATTEMPTS));
    }

    public void uploadPhotos(List<EditablePhoto> photos) {
        configuration.getPhotoStocks().stream().map(PhotoStock::ftpEndpoint)
                .forEach(endpoint -> CompletableFuture.runAsync(() -> uploadPhotos(photos, endpoint)));
    }

    private void uploadPhotos(List<EditablePhoto> photos, PhotoStockAccess endpoint) {
        photos.forEach(photo -> {
            notifier.notifyProgress(photo, endpoint, 0);
            Mono.fromRunnable(taskFactory.create(photo, endpoint, notifier))
                    .doOnSuccess(c -> notifier.notifySuccess(photo, endpoint))
                    .doOnError(UploadException.class, ex -> notifier.notifyError(photo, endpoint, ex)).retry(attempts)
                    .subscribe();
        });
    }

}
