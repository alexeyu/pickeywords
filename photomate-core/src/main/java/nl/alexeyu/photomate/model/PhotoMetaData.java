package nl.alexeyu.photomate.model;

import java.util.Collection;

public interface PhotoMetaData {
    
    Object getProperty(PhotoProperty p);

    Collection<String> keywords();
    
    String description();
    
    String caption();

}