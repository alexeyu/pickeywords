package nl.alexeyu.photomate.api;

import java.util.List;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoStockApi {
	
	List<Photo> search(String keyword);

	ImageIcon getImage(String photoUrl);
	
	List<String> getKeywords(String photUrl);

}
