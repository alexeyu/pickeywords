package nl.alexeyu.photomate.api;

import java.io.File;
import java.nio.file.Path;

public final class PhotoFileCleaner implements PhotoFileProcessor {
    
    private final String suffix;
    
    public PhotoFileCleaner() {
        this("");
    }

    public PhotoFileCleaner(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void process(Path photoPath) {
        new File(photoPath.toString() + suffix).deleteOnExit();
    }

}
