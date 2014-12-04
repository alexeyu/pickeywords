package nl.alexeyu.photomate.model;

import java.util.Optional;

import javax.swing.ImageIcon;

public interface Photo {
    
    String name();
    
    Optional<ImageIcon> thumbnail();
    
    Optional<PhotoMetaData> metaData();

}