package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.LocalPhoto;

public final class ArchivePhoto extends LocalPhoto {
	
	private boolean toBeDeleted = false;
	
	public ArchivePhoto(Path file) {
		super(file);
	}

	public void delete() {
		toBeDeleted = true;
		getPath().toFile().deleteOnExit();
	}

	public boolean isDeleted() {
		return toBeDeleted;
	}

}
