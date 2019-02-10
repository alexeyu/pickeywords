package nl.alexeyu.photomate.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

public class DefaultPhotoMetaData implements PhotoMetaData {
	
    private final Map<PhotoProperty, Object> properties = new HashMap<>();
    
    DefaultPhotoMetaData(Map<PhotoProperty, Object> properties) {
    	this.properties.putAll(properties);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> keywords() {
    	var keywords = (Collection<String>) properties.get(PhotoProperty.KEYWORDS);
    	return keywords == null ? List.of() : keywords;
    }
    
	@Override
	public Object getProperty(PhotoProperty p) {
		if (p == PhotoProperty.KEYWORDS) {
			return keywords();
		}
		String result = (String) properties.get(p);
		return Strings.nullToEmpty(result);
	}

	@Override
	public String description() {
		return getProperty(PhotoProperty.DESCRIPTION).toString();
	}

	@Override
	public String caption() {
		return getProperty(PhotoProperty.CAPTION).toString();
	}

}
