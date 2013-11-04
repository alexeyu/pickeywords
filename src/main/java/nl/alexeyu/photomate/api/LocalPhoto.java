package nl.alexeyu.photomate.api;

import java.io.File;

public class LocalPhoto extends AbstractPhoto {
	
	private final File file;
	
	LocalPhoto(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
    public String getName() {
		return file.getName();
	}

    public boolean isReadyToUpload() {
		return getThumbnail() != null && getMetaData() != null && getMetaData().isComplete();
	}
	
	@Override
	public String toString() {
		return "My Photo [" + getName() + "]";
	}

}
