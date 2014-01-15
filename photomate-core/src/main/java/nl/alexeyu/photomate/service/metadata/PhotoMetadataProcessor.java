package nl.alexeyu.photomate.service.metadata;

import nl.alexeyu.photomate.model.PhotoMetaData;

public interface PhotoMetadataProcessor extends PhotoMetadataReader {

    void update(String photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData);

}
