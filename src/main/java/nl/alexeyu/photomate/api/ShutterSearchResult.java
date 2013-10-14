package nl.alexeyu.photomate.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

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

    public static void main(String[] args) throws Exception  {
        System.out.println(new ObjectMapper().readValue("{\"count\": 10, \"results\": []}", ShutterSearchResult.class));
        
    }
}
