package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.LocalPhotoFactory;

public final class ArchivePhotoFactory implements LocalPhotoFactory<ArchivePhoto> {

	@Override
	public ArchivePhoto createPhoto(Path path) {
		return new ArchivePhoto(path);
	}

}
