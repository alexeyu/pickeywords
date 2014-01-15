package nl.alexeyu.photomate.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DefaultPhotoMetaData implements PhotoMetaData {
    
    private String name;
    
    private String description;
    
    private String creator;
    
    private List<String> keywords;
    
    public DefaultPhotoMetaData(String name, String description,String creator, List<String> keywords) {
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
    public List<String> getKeywords() {
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
