package nl.alexeyu.photomate.api.editable;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.LocalPhotoFactory;

public final class EditablePhotoFactory implements LocalPhotoFactory<EditablePhoto> {

	@Override
	public EditablePhoto createPhoto(Path path) {
		return new EditablePhoto(path);
	}

}
