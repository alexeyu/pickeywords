package nl.alexeyu.photomate.service;

import java.awt.Image;

public interface ThumbnailProvider {
    
    Image getThumbnail(String photoPath) throws Exception;

}
