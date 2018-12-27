package nl.alexeyu.photomate.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import com.google.inject.name.Named;

import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.archive.ArchivePhoto;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.MediaFileProcessors;

public class ArchivePhotoContainer extends PhotoContainer<ArchivePhoto> {
    
    private static final int COLUMN_COUNT = 4;
    
    @Inject
    private ConfigReader configReader;
    
    @Inject
    @Named("archiveApi")
    private LocalPhotoApi<ArchivePhoto> photoApi;

    public ArchivePhotoContainer() {
        super(COLUMN_COUNT);
    }

    @Override
	protected PhotoTable<ArchivePhoto> createPhotoTable(int columnCount) {
		return new ArchivePhotoTable(columnCount, this);
	}

	@Inject
    public void init() throws IOException {
        configReader.getProperty("archiveFolder").ifPresent(arcFolder -> {
            var dir = Paths.get(arcFolder);
            if (Files.exists(dir)) {
                readPhotos(dir);
            }
        });
    }

    private void readPhotos(Path dir) {
        var photos = photoApi.createPhotos(MediaFileProcessors.JPEG.apply(dir), ArchivePhoto::new);
        photoTable.setPhotos(photos);
    }

}
