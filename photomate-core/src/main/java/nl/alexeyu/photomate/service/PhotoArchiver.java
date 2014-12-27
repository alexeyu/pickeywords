package nl.alexeyu.photomate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.PhotoFileProcessor;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.upload.UploadPhotoListener;
import nl.alexeyu.photomate.util.ConfigReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhotoArchiver implements PhotoFileProcessor, UploadPhotoListener {

    private Optional<String> archiveDir;
    
    @Inject
    private ConfigReader configReader;
    
    private ConcurrentHashMap<String, Boolean> archivedPhotos = new ConcurrentHashMap<>();
    
    @Override
    public void process(Path photoPath) {
        archiveDir = configReader.getProperty("archiveFolder");
        if (archivedPhotos.put(photoPath.toString(), Boolean.TRUE) == null) {
            archiveDir.ifPresent(dir ->
                CompletableFuture.runAsync(new ArchivePhotoTask(photoPath, Paths.get(dir)))
            );
        }
    }
    
    @Override
    public void onProgress(PhotoStock photoStock, EditablePhoto photo, long bytesUploaded) {}

    @Override
    public void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft) {}

    @Override
    public void onSuccess(PhotoStock photoStock, EditablePhoto photo) {
        process(photo.getPath());
    }

    private static class ArchivePhotoTask implements Runnable {

        private final Logger logger = LoggerFactory.getLogger("ArchivePhotoTask");
        
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
