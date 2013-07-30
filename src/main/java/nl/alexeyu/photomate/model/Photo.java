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
		return file.getName();
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

}
