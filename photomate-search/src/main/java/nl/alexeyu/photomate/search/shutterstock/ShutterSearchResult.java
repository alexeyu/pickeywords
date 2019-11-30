package nl.alexeyu.photomate.search.shutterstock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
final class ShutterSearchResult {
    
    private List<ShutterPhotoDescription> photoDescriptions;

    @JsonProperty("results")
    public void setPhotoDescriptions(List<ShutterPhotoDescription> photoDescriptions) {
        this.photoDescriptions = photoDescriptions;
    }

    public List<ShutterPhotoDescription> getPhotoDescriptions() {
        return photoDescriptions;
    }

}
