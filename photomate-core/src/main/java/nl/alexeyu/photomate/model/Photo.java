package nl.alexeyu.photomate.model;

import javax.swing.ImageIcon;

public interface Photo {

    String name();

    ImageIcon thumbnail();

    PhotoMetaData metaData();

}