package nl.alexeyu.photomate.api;

import nl.alexeyu.photomate.model.PhotoProperty;

public interface LocalPhotoUpdater {

    void updateProperty(LocalPhoto photo, PhotoProperty property, Object propertyValue);

}
