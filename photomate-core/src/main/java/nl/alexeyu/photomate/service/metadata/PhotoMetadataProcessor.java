package nl.alexeyu.photomate.service.metadata;

import java.nio.file.Path;

import nl.alexeyu.photomate.model.PhotoMetaData;

public interface PhotoMetadataProcessor extends PhotoMetadataReader {

    void update(Path photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData);

}
