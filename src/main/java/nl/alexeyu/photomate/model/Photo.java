package nl.alexeyu.photomate.model;

import javax.swing.ImageIcon;

public interface Photo {
    
    String getName();

    ImageIcon getThumbnail();
    
    PhotoMetaData getMetaData();

}