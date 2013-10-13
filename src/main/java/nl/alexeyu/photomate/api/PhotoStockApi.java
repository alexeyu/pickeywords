package nl.alexeyu.photomate.api;

import java.util.List;

import javax.swing.Icon;

import nl.alexeyu.photomate.model.StockPhotoDescription;

public interface PhotoStockApi {
	
	List<StockPhotoDescription> search(String keyword);

	Icon getImage(StockPhotoDescription photo);
	
	List<String> getKeywords(StockPhotoDescription photo);

}
