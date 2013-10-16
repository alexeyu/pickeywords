package nl.alexeyu.photomate.api;

import java.util.List;

import nl.alexeyu.photomate.model.Photo;

public interface PhotoStockApi {
	
	List<Photo> search(String keyword);

}
