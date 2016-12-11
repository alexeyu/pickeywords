package nl.alexeyu.photomate.search.shutterstock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShutterSearchResult {
    
    int count;
    
    List<ShutterPhotoDescription> photoDescriptions;

    @JsonProperty("count")
    public void setCount(int count) {
        this.count = count;
    }
    
    @JsonProperty("results")
    public void setPhotoDescriptions(List<ShutterPhotoDescription> photoDescriptions) {
        this.photoDescriptions = photoDescriptions;
    }

    public int getCount() {
        return count;
    }

    public List<ShutterPhotoDescription> getPhotoDescriptions() {
        return photoDescriptions;
    }

}
