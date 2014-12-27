package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.PhotoFactory;

public final class ArchivePhotoFactory implements PhotoFactory<Path, ArchivePhoto> {
    
	@Override
	public ArchivePhoto createPhoto(Path path) {
		return new ArchivePhoto(path);
	}

}
