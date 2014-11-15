package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.LocalPhotoApi;

public class ArchivePhotoApi extends LocalPhotoApi<ArchivePhoto> {

	@Override
	protected ArchivePhoto createPhoto(Path path) {
		return new ArchivePhoto(path);
	}
	
}
