package nl.alexeyu.photomate.service;

import nl.alexeyu.photomate.api.AbstractPhoto;

public interface PhotoContainer<T extends AbstractPhoto> {

    T getPhoto();
    
}
