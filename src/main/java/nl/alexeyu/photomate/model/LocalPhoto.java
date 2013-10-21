package nl.alexeyu.photomate.model;

import java.io.File;
import java.util.List;

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

	public void addKeywords(List<String> keywords) {
		getKeywords().addAll(keywords);
	}
	
	public void removeKeywords(List<String> keywords) {
		getKeywords().removeAll(keywords);
	}

    public boolean isReadyToUpload() {
		return hasKeywords() && hasThumbnail();
	}
	
	@Override
	public String toString() {
		return "My Photo [" + getName() + "]";
	}

}
