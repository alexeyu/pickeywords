package nl.alexeyu.photomate.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

public final class ExtensionBasedMediaFilesProcessor implements Predicate<Path>, Function<Path, Stream<Path>> {

	private final Set<String> extensions;
	
	public ExtensionBasedMediaFilesProcessor(String... extensions) {
		this.extensions = Sets.newHashSet(extensions);
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
        var fileName = path.getFileName()
                .toString()
                .toLowerCase();
        return extensions.stream().anyMatch(fileName::endsWith);
	}

}
