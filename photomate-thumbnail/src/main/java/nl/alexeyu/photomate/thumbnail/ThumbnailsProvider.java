package nl.alexeyu.photomate.thumbnail;

import java.nio.file.Path;
import java.util.List;

import javax.swing.ImageIcon;

public interface ThumbnailsProvider {
    
    List<ImageIcon> apply(Path path);

}
