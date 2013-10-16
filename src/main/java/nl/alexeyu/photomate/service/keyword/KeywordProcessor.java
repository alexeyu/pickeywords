package nl.alexeyu.photomate.service.keyword;


public interface KeywordProcessor extends KeywordReader {

	void addKeyword(String photoPath, String keyword);
	
	void removeKeyword(String photoPath, String keyword);
	
}
