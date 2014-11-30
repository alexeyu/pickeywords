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

	@Override
    public String getName() {
		return path.getFileName().toString();
	}

	@Override
	public String toString() {
		return getName();
	}

}
