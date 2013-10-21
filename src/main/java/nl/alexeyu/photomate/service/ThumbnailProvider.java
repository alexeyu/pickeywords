package nl.alexeyu.photomate.service;

import java.awt.Image;
import java.io.File;

public interface ThumbnailProvider {
    
    Image getThumbnail(File photoFile) throws Exception;

}
