package nl.alexeyu.photomate.api;

import java.io.File;

import nl.alexeyu.photomate.model.DeletablePhoto;

public class ArchivePhoto extends LocalPhoto implements DeletablePhoto {
	
	private boolean toBeDeleted = false;
	
	public ArchivePhoto(File file) {
		super(file);
	}

	@Override
	public void delete() {
		toBeDeleted = true;
		getFile().deleteOnExit();
	}

	@Override
	public boolean isDeleted() {
		return toBeDeleted;
	}
	
}
