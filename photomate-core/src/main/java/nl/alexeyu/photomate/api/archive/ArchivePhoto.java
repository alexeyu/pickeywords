package nl.alexeyu.photomate.api.archive;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.api.PhotoFileProcessor;

public final class ArchivePhoto extends LocalPhoto {
	
	private boolean toBeDeleted = false;
	
	private final PhotoFileProcessor cleaner;
	
	public ArchivePhoto(Path path, PhotoFileProcessor cleaner) {
		super(path);
		this.cleaner = cleaner;
	}

	public void delete() {
		toBeDeleted = true;
		cleaner.process(getPath());
	}

	public boolean isDeleted() {
		return toBeDeleted;
	}

}
