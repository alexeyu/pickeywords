package nl.alexeyu.photomate.service.keyword;

import java.util.List;


public interface KeywordProcessor extends KeywordReader {

	void addKeywords(String photoPath, List<String> keyword);
	
	void removeKeywords(String photoPath, List<String> keyword);
	
}
