package nl.alexeyu.photomate.api;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class PhotoFileCleaner implements Consumer<Path> {
    
    private final Collection<String> suffixes;
    
    public PhotoFileCleaner(String... suffixes) {
        this.suffixes = List.of(suffixes);
    }

    @Override
    public void accept(Path photoPath) {
        suffixes.stream()
            .map(suffix -> photoPath.toString() + suffix)
            .map(File::new)
            .forEach(File::deleteOnExit);
    }

}
