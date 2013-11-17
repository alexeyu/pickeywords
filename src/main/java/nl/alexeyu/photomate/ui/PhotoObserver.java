package nl.alexeyu.photomate.ui;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoObserver<P extends Photo> {
    void photoSelected(P photo);
}
