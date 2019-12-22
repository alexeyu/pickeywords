package nl.alexeyu.photomate.service.archive;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.files.FileManager;
import nl.alexeyu.photomate.thumbnail.FileThumbnailsProvider;
import nl.alexeyu.photomate.upload.UploadSuccessEvent;
import nl.alexeyu.photomate.util.Configuration;
import nl.alexeyu.photomate.util.MediaFileProcessors;

public class PhotoArchiver implements Consumer<Path> {

    private ConcurrentSkipListSet<String> archivedPhotos = new ConcurrentSkipListSet<>();

    @Inject
    private Configuration configuration;

    private PhotoFileCleaner photoFileCleaner = new PhotoFileCleaner("", FileThumbnailsProvider.CACHE_SUFFIX);
    
    private Path archiveFolder;

    @Inject
    public void init() {
        archiveFolder = configuration.getArchiveFolder();
        CompletableFuture.runAsync(new CleanupOldPhotos());
    }

    @Override
    public void accept(Path photoPath) {
        if (MediaFileProcessors.JPEG.test(photoPath)
                && !archivedPhotos.add(photoPath.toString())) {
            CompletableFuture.runAsync(() -> FileManager.copy(List.of(photoPath), archiveFolder));
        }
    }

    @Subscribe
    public void onSuccess(UploadSuccessEvent ev) {
        accept(ev.getPhoto().getPath());
    }

    private class CleanupOldPhotos implements Runnable {

        @Override
        public void run() {
            var archiveCapacity = Integer.valueOf(configuration.getProperty("archiveCapacity").orElse("50"));
            MediaFileProcessors.JPEG.apply(archiveFolder)
                .sorted((a, b) -> Long.compare(b.toFile().lastModified(), a.toFile().lastModified()))
                .skip(archiveCapacity)
                .forEach(photoFileCleaner);
        }

    }

}
