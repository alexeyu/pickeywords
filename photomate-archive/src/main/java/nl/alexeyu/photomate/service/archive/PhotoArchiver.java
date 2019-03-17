package nl.alexeyu.photomate.service.archive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.thumbnail.FileThumbnailsProvider;
import nl.alexeyu.photomate.upload.UploadSuccessEvent;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.MediaFileProcessors;

public class PhotoArchiver implements Consumer<Path> {

    private ConcurrentSkipListSet<String> archivedPhotos = new ConcurrentSkipListSet<>();

    @Inject
    private ConfigReader configReader;

    private PhotoFileCleaner photoFileCleaner = new PhotoFileCleaner("", FileThumbnailsProvider.CACHE_SUFFIX);
    
    private Path archiveFolder;

    @Inject
    public void init() {
        archiveFolder = configReader.getProperty("archiveFolder").map(Paths::get).orElse(createTempDir());
        CompletableFuture.runAsync(new CleanupOldPhotos());
    }

    private Path createTempDir() {
        try {
            return Files.createTempDirectory("archive");
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void accept(Path photoPath) {
        if (MediaFileProcessors.JPEG.test(photoPath)
                && !archivedPhotos.add(photoPath.toString())) {
            CompletableFuture.runAsync(new ArchivePhotoTask(photoPath, archiveFolder));
        }
    }

    @Subscribe
    public void onSuccess(UploadSuccessEvent ev) {
        accept(ev.getPhoto().getPath());
    }

    private class CleanupOldPhotos implements Runnable {

        @Override
        public void run() {
            var archiveCapacity = Integer.valueOf(configReader.getProperty("archiveCapacity").orElse("50"));
            MediaFileProcessors.JPEG.apply(archiveFolder)
                .sorted((a, b) -> Long.compare(b.toFile().lastModified(), a.toFile().lastModified()))
                .skip(archiveCapacity)
                .forEach(photoFileCleaner);
        }

    }

    private static class ArchivePhotoTask implements Runnable {

        private final Logger logger = LogManager.getLogger();

        private final Path photoPath;

        private final Path archiveFolder;

        public ArchivePhotoTask(Path photoPath, Path archiveFolder) {
            this.photoPath = photoPath;
            this.archiveFolder = archiveFolder;
        }

        @Override
        public void run() {
            try {
                Files.createDirectories(archiveFolder);
                Path archivePath = archiveFolder.resolve(photoPath.getFileName());
                Files.copy(photoPath, archivePath);
            } catch (IOException ex) {
                logger.error("Error on copying file", ex);
            }
        }

    }

}
