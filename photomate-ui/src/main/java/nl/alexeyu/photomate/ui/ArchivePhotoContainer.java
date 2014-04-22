package nl.alexeyu.photomate.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.ArchivePhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.util.ConfigReader;

public class ArchivePhotoContainer extends PhotoContainer<ArchivePhoto> {
    
    private static final int COLUMN_COUNT = 4;
    
    @Inject
    private ConfigReader configReader;
    
    @Inject
    private LocalPhotoApi localPhotoApi;

    @Inject
    private PhotoFactory photoFactory;
    
    public ArchivePhotoContainer() {
        super(COLUMN_COUNT);
    }

    @Inject
    public void init() throws IOException {
        String archiveFolder = configReader.getProperty("archiveFolder", null);
        if (archiveFolder != null) {
            File dir = new File(archiveFolder);
            if (dir.exists()) {
                List<ArchivePhoto> photos = photoFactory.createLocalPhotos(dir, localPhotoApi, ArchivePhoto.class);
                photoTable.setPhotos(photos);
            }
        }
    }
    
}
