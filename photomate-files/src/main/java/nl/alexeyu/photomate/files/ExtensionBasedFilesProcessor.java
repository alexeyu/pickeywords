package nl.alexeyu.photomate.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ExtensionBasedFilesProcessor implements Predicate<Path>, Function<Path, Stream<Path>> {

    private final Set<String> extensions;

    public ExtensionBasedFilesProcessor(String... extensions) {
        this.extensions = Set.of(extensions);
    }

    @Override
    public Stream<Path> apply(Path dir) {
        try {
            return Files.list(dir).filter(this);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public boolean test(Path path) {
        var fileName = path.getFileName().toString().toLowerCase();
        return extensions.stream().anyMatch(fileName::endsWith);
    }

}
