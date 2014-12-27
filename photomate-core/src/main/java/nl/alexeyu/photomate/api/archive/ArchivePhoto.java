package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.LocalPhoto;

public final class ArchivePhoto extends LocalPhoto {
	
	private boolean toBeDeleted = false;
	
	public ArchivePhoto(Path path) {
		super(path);
	}

	public void delete() {
		toBeDeleted = true;
	}

	public boolean isDeleted() {
		return toBeDeleted;
	}

}
