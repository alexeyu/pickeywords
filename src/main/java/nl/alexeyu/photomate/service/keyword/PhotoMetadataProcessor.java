package nl.alexeyu.photomate.service.keyword;

import nl.alexeyu.photomate.model.PhotoMetaData;

public interface PhotoMetadataProcessor extends PhotoMetadataReader {

    void update(String photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData);

}
