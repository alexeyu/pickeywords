package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Image;
import java.io.File;

import org.apache.commons.lang3.tuple.Pair;

public interface ThumbnailProvider {
    
    Pair<Image, Image> getThumbnails(File photoFile);

}
