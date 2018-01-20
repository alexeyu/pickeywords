package nl.alexeyu.photomate.service.archive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import nl.alexeyu.photomate.service.upload.UploadSuccessEvent;
import nl.alexeyu.photomate.util.ConfigReader;

public class PhotoArchiver implements Consumer<Path> {

    @Inject
    private ConfigReader configReader;

    private ConcurrentHashMap<String, Boolean> archivedPhotos = new ConcurrentHashMap<>();

    @Override
    public void accept(Path photoPath) {
        Optional<String> archiveDir = configReader.getProperty("archiveFolder");
        if (archivedPhotos.put(photoPath.toString(), Boolean.TRUE) == null) {
            archiveDir.ifPresent(dir -> CompletableFuture.runAsync(new ArchivePhotoTask(photoPath, Paths.get(dir))));
        }
    }

    @Subscribe
    public void onSuccess(UploadSuccessEvent ev) {
        accept(ev.getPhoto().getPath());
    }

    private static class ArchivePhotoTask implements Runnable {

        private final Logger logger = LogManager.getLogger();

        private final Path photoPath;

        private final Path directory;

        public ArchivePhotoTask(Path photoPath, Path directory) {
            this.photoPath = photoPath;
            this.directory = directory;
        }

        @Override
        public void run() {
            try {
                Files.createDirectories(directory);
                Path archivePath = directory.resolve(photoPath.getFileName());
                Files.copy(photoPath, archivePath);
            } catch (IOException ex) {
                logger.error("Error on copying file", ex);
            }
        }

    }

}
