package nl.alexeyu.photomate.service.metadata;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;

public interface PhotoMetadataReader {

	DefaultPhotoMetaData read(String photoUrl);
	
}
