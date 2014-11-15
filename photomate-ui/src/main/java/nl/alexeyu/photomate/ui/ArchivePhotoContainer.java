package nl.alexeyu.photomate.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.archive.ArchivePhoto;
import nl.alexeyu.photomate.api.archive.ArchivePhotoApi;
import nl.alexeyu.photomate.util.ConfigReader;

public class ArchivePhotoContainer extends PhotoContainer<ArchivePhoto> {
    
    private static final int COLUMN_COUNT = 4;
    
    @Inject
    private ConfigReader configReader;
    
    @Inject
    private ArchivePhotoApi photoApi;

    public ArchivePhotoContainer() {
        super(COLUMN_COUNT);
    }

    @Inject
    public void init() throws IOException {
        String archiveFolder = configReader.getProperty("archiveFolder", null);
        if (archiveFolder != null) {
            Path dir = Paths.get(archiveFolder);
            if (Files.exists(dir)) {
                List<ArchivePhoto> photos = photoApi.createPhotos(dir);
                photoTable.setPhotos(photos);
            }
        }
    }
    
}
