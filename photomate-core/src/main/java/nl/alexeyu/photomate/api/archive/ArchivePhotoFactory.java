package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.api.PhotoFileProcessor;

public final class ArchivePhotoFactory implements PhotoFactory<Path, ArchivePhoto> {
    
    private final PhotoFileProcessor cleaner = new PhotoFileCleaner();

	@Override
	public ArchivePhoto createPhoto(Path path) {
		return new ArchivePhoto(path, cleaner);
	}

}
