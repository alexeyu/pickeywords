package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Image;

public class Thumbnails {
	
	private final Image thumbnail, preview;

	public Thumbnails(Image thumbnail, Image preview) {
		this.thumbnail = thumbnail;
		this.preview = preview;
	}

	public Image getThumbnail() {
		return thumbnail;
	}

	public Image getPreview() {
		return preview;
	}

}
