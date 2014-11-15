package nl.alexeyu.photomate.api.editable;

import java.nio.file.Path;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.service.thumbnail.Thumbnails;

public final class EditablePhotoApi extends LocalPhotoApi<EditablePhoto> {

	@Override
	protected EditablePhoto createPhoto(Path path) {
		return new EditablePhoto(path);
	}

	@Override
	protected void setThumbnails(EditablePhoto photo, Thumbnails images) {
		super.setThumbnails(photo, images);
		photo.setPreview(new ImageIcon(images.getPreview()));
	}
	
}
