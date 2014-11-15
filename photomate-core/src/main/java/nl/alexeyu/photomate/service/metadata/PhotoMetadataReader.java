package nl.alexeyu.photomate.service.metadata;

import java.nio.file.Path;

import nl.alexeyu.photomate.model.PhotoMetaData;

public interface PhotoMetadataReader {

	PhotoMetaData read(Path photoPath);
	
}
