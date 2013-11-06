package nl.alexeyu.photomate.model;

import java.awt.Dimension;

import javax.swing.ImageIcon;

public interface Photo {
    
    Dimension THUMBNAIL_SIZE = new Dimension(160, 112);
    
    Dimension PREVIEW_SIZE = new Dimension(360, 240);

    String getName();

    ImageIcon getThumbnail();
    
    ImageIcon getPreview();

    PhotoMetaData getMetaData();

}