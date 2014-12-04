package nl.alexeyu.photomate.service;

import java.util.Optional;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoObserver<P extends Photo> {
    void photoSelected(Optional<P> photo);
}
