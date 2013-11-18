package nl.alexeyu.photomate.ui;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.util.ConfigReader;

public class ArchivePhotoSource extends PhotoSource<LocalPhoto> {
    
    private static final int COLUMN_COUNT = 4;
    
    @Inject
    private ConfigReader configReader;
    
    @Inject
    private LocalPhotoApi localPhotoApi;

    @Inject
    private PhotoFactory photoFactory;
    
    public ArchivePhotoSource() {
        super(COLUMN_COUNT);
    }

    @Inject
    public void init() {
        String archiveFolder = configReader.getProperty("archiveFolder", null);
        if (archiveFolder != null) {
            File dir = new File(archiveFolder);
            if (dir.exists()) {
                List<LocalPhoto> photos = photoFactory.createLocalPhotos(dir, localPhotoApi, LocalPhoto.class);
                photoTable.setPhotos(photos);
            }
        }
    }
    
}
