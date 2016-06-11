package nl.alexeyu.photomate.api;

import java.nio.file.Path;

public abstract class LocalPhoto extends AbstractPhoto {

    private final Path path;

    public LocalPhoto(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public long fileSize() {
        return path.toFile().length();
    }

    @Override
    public String name() {
        return path.getFileName().toString();
    }

    @Override
    public String toString() {
        return name();
    }

}
