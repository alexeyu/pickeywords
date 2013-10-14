package nl.alexeyu.photomate.service.keyword;

import nl.alexeyu.photomate.model.LocalPhoto;

public interface KeywordReader {

	void readKeywords(LocalPhoto photo);
	
	void addKeyword(LocalPhoto photo, String keyword);
	
	void removeKeyword(LocalPhoto photo, String keyword);
	
}
