package nl.alexeyu.photomate.model;

import java.util.List;

import javax.swing.ImageIcon;

public interface Photo {

    String getName();

    ImageIcon getThumbnail();

    List<String> getKeywords();

}