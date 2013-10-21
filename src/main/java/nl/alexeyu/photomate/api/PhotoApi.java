package nl.alexeyu.photomate.api;

import java.util.List;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.ResultProcessor;

public interface PhotoApi<P extends Photo> {
    
    void provideThumbnail(P photo, ResultProcessor<ImageIcon> processor);
    
    void provideKeywords(P photo, ResultProcessor<List<String>> processor);

}
