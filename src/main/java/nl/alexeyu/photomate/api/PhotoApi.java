package nl.alexeyu.photomate.api;

import java.util.List;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.ResultFiller;

public interface PhotoApi {
    
    void provideThumbnail(String tumbnailUrl, ResultFiller<ImageIcon> filler);
    
    void provideKeywords(String url, ResultFiller<List<String>> keywords);

}
