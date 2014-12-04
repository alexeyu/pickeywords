package nl.alexeyu.photomate.model;

import static nl.alexeyu.photomate.model.PhotoProperty.CAPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.DESCRIPTION;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultPhotoMetaData implements PhotoMetaData {
	
    private final Map<PhotoProperty, Object> properties = new HashMap<>();
    
    public DefaultPhotoMetaData(Map<PhotoProperty, Object> properties) {
    	for (PhotoProperty pp : PhotoProperty.values()) {
    		this.properties.put(pp, properties.get(pp));
    	}
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> keywords() {
    	Collection<String> keywords = (Collection<String>) properties.get(PhotoProperty.KEYWORDS);
    	return keywords == null ? Collections.emptyList() : keywords;
    }
    
	@Override
	public Object getProperty(PhotoProperty p) {
		return properties.get(p);
	}

	@Override
	public String description() {
		return properties.get(DESCRIPTION).toString();
	}

	@Override
	public String caption() {
		return properties.get(CAPTION).toString();
	}

}
