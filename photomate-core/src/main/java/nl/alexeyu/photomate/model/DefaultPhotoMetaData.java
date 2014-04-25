package nl.alexeyu.photomate.model;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class DefaultPhotoMetaData implements PhotoMetaData {
    
    private String name;
    
    private String description;
    
    private String creator;
    
    private Collection<String> keywords;
    
    public DefaultPhotoMetaData(String name, String description,String creator, Collection<String> keywords) {
        this.name = name;
        this.description = description;
        this.keywords = keywords;
        this.creator = creator;
    }

    @Override
    public String getCaption() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Collection<String> getKeywords() {
        return keywords;
    }
    
    @Override
    public String getCreator() {
        return creator;
    }

    public boolean isComplete() {
        return StringUtils.isNotBlank(name) 
                && StringUtils.isNotBlank(description)
                && StringUtils.isNotBlank(creator)
                && keywords != null 
                && keywords.size() > 0
                && keywords.size() <= 50;
    }
    
}
