package nl.alexeyu.photomate.model;

import java.util.Collection;

public interface PhotoMetaData {
    
    Object getProperty(PhotoProperty p);

    Collection<String> getKeywords();
    
    String getDescription();
    
    String getCaption();

}