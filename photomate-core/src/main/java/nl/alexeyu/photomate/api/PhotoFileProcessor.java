package nl.alexeyu.photomate.api;

import java.nio.file.Path;

public interface PhotoFileProcessor {
    
    void process(Path photoPath);

}
