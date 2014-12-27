package nl.alexeyu.photomate.api.editable;

import java.nio.file.Path;

import nl.alexeyu.photomate.api.PhotoFactory;

public final class EditablePhotoFactory implements PhotoFactory<Path, EditablePhoto> {

	@Override
	public EditablePhoto createPhoto(Path path) {
		return new EditablePhoto(path);
	}

}
