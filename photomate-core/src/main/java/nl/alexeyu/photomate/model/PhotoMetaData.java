package nl.alexeyu.photomate.model;

import java.util.List;

public interface PhotoMetaData {
    
    String KEYWORDS_PROPERTY = "keywords";
    String CAPTION_PROPERTY = "caption";
    String DESCRIPTION_PROPERTY = "description";

    String getCaption();

    String getDescription();
    
    String getCreator();

    List<String> getKeywords();
    
    boolean isComplete();

}