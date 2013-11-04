package nl.alexeyu.photomate.api;

import java.util.List;

public interface PhotoStockApi {
	
	List<RemotePhoto> search(String keyword);

}
