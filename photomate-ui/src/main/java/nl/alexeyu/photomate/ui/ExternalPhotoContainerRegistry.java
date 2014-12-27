package nl.alexeyu.photomate.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.ui.PhotoContainer;

public class ExternalPhotoContainerRegistry {
    
    private final Map<String, PhotoContainer<? extends AbstractPhoto>> photoSources = new LinkedHashMap<>();
    
    public void registerPhotoSource(String name, PhotoContainer<?> photoSource) {
        photoSources.put(name, photoSource);
    }
    
    public Collection<String> getSourceNames() {
        return photoSources.keySet();
    }

    public PhotoContainer<?> getPhotoSource(String name) {
        return photoSources.get(name);
    }
}
