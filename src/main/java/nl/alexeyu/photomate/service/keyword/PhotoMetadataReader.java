package nl.alexeyu.photomate.service.keyword;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;

public interface PhotoMetadataReader {

	DefaultPhotoMetaData read(String photoUrl);
	
}
