package nl.alexeyu.photomate.service.keyword;

import java.util.List;

public interface KeywordReader {

	List<String> readKeywords(String photoUrl);
	
}
