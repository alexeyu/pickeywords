package nl.alexeyu.photomate.api;

import java.nio.file.Path;

public interface LocalPhotoFactory<P extends LocalPhoto> {

	P createPhoto(Path path);
}
