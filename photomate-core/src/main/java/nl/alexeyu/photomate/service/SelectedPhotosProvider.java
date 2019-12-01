package nl.alexeyu.photomate.service;

import java.util.List;

import nl.alexeyu.photomate.api.editable.EditablePhoto;

public interface SelectedPhotosProvider {

    List<EditablePhoto> getSelectedPhotos();

    void clearSelectedPhotos();

}
