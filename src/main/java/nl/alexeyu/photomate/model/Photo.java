package nl.alexeyu.photomate.model;

import java.awt.Dimension;

import javax.swing.ImageIcon;

public interface Photo {
    
    Dimension THUMBNAIL_SIZE = new Dimension(180, 135);
    
    Dimension PREVIEW_SIZE = new Dimension(360, 270);

    String getName();

    ImageIcon getThumbnail();
    
    ImageIcon getPreview();

    PhotoMetaData getMetaData();

}