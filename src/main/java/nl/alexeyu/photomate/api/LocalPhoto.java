package nl.alexeyu.photomate.api;

import java.io.File;

public class LocalPhoto extends AbstractPhoto {
	
	private final File file;
	
	public LocalPhoto(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
    public String getName() {
		return file.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

}
