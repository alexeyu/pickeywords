package nl.alexeyu.photomate.api;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class PhotoFileCleaner implements Consumer<Path> {
    
    private final String suffix;
    
    public PhotoFileCleaner() {
        this("");
    }

    public PhotoFileCleaner(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void accept(Path photoPath) {
        new File(photoPath.toString() + suffix).deleteOnExit();
    }

}
