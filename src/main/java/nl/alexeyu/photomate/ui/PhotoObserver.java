package nl.alexeyu.photomate.ui;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoObserver {
    void photoSelected(Photo photo);
}
