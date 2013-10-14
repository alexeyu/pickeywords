package nl.alexeyu.photomate.ui;

import javax.swing.AbstractListModel;

import nl.alexeyu.photomate.model.Photo;

public class KeywordListModel extends AbstractListModel<String> {
    
    private final Photo photo;
    
    public KeywordListModel(Photo photo) {
        this.photo = photo;
    }

    public int getSize() {
        return photo.getKeywords().size();
    }

    public String getElementAt(int index) {
        return photo.getKeywords().get(index);
    }
    
}
