package nl.alexeyu.photomate.service.keyword;

import nl.alexeyu.photomate.model.Photo;

public interface KeywordReader {

	void readKeywords(Photo photo);
	
	void addKeyword(Photo photo, String keyword);
	
	void removeKeyword(Photo photo, String keyword);
	
}
