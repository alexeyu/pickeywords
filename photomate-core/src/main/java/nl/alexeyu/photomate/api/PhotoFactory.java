package nl.alexeyu.photomate.api;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoFactory<S, P extends Photo> {

	P createPhoto(S source);
}
