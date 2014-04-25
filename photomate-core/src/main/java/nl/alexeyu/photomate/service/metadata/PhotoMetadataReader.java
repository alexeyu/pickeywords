package nl.alexeyu.photomate.service.metadata;

import nl.alexeyu.photomate.model.PhotoMetaData;

public interface PhotoMetadataReader {

	PhotoMetaData read(String photoUrl);
	
}
