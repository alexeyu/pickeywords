package nl.alexeyu.photomate.model;

import java.io.File;

import nl.alexeyu.photomate.api.PhotoApi;
import nl.alexeyu.photomate.service.UpdateListener;

public class LocalPhoto extends AbstractPhoto {
	
	private final File file;
	
	public LocalPhoto(File file, PhotoApi photoApi) {
	    super(photoApi);
		this.file = file;
		getKeywords();
		getThumbnail();
	}

	public File getFile() {
		return file;
	}

	@Override
    public String getName() {
		return file.getName();
	}

	public void addKeyword(String keyword) {
		getKeywords().add(keyword);
	}
	
	public void removeKeyword(String keyword) {
		getKeywords().remove(keyword);
	}

	@Override
    protected String getThumbnailUrl() {
        return file.getAbsolutePath();
    }

    @Override
    protected String getUrl() {
        return file.getAbsolutePath();
    }

    public boolean isReadyToUpload() {
		return hasKeywords() && hasThumbnail();
	}
	
	@Override
	public String toString() {
		return "My Photo [" + getName() + "]";
	}

}
