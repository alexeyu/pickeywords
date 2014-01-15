package nl.alexeyu.photomate.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.alexeyu.photomate.ui.PhotoSource;

public class ExternalPhotoSourceRegistry {
    
    private final Map<String, PhotoSource<?>> photoSources = new LinkedHashMap<>();
    
    public void registerPhotoSource(String name, PhotoSource<?> photoSource) {
        photoSources.put(name, photoSource);
    }
    
    public Collection<String> getSourceNames() {
        return photoSources.keySet();
    }

    public PhotoSource<?> getPhotoSource(String name) {
        return photoSources.get(name);
    }
}
