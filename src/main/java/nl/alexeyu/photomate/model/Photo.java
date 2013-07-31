package nl.alexeyu.photomate.model;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Photo {
	
	public static Photo NULL_PHOTO = new Photo(null);

	private final File file;
	
	private List<String> keywords = new ArrayList<>();
	
	private AtomicReference<Image> thumbnail = new AtomicReference<>();

	public Photo(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return file != null ? file.getName() : null;
	}
	
	public void setThumbnail(Image img) {
		thumbnail.set(img);
	}

	public Image getThumbnail() {
		return thumbnail.get();
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public void addKeyword(String keyword) {
		this.keywords.add(keyword);
	}
	
	public void removeKeyword(String keyword) {
		this.keywords.remove(keyword);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Photo [" + getName() + "]";
	}

	
}
