package nl.alexeyu.photomate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DefaultPhotoMetaDataBuilder {

    private Map<PhotoProperty, Object> properties = new HashMap<>();

    public DefaultPhotoMetaDataBuilder() {
    }

    public DefaultPhotoMetaDataBuilder(Map<PhotoProperty, String> properties) {
        this.properties.putAll(properties);
    }

    public DefaultPhotoMetaDataBuilder(PhotoMetaData metaData) {
        Stream.of(PhotoProperty.values()).forEach(pp -> properties.put(pp, metaData.getProperty(pp)));
    }

    public DefaultPhotoMetaDataBuilder set(PhotoProperty property, Object value) {
        properties.put(property, value);
        return this;
    }

    public DefaultPhotoMetaData build() {
        return new DefaultPhotoMetaData(properties);
    }

}
