package nl.alexeyu.photomate.search.api;

import java.util.List;

public interface PhotoStockApi {

    List<RemotePhoto> search(String keywords);

}
