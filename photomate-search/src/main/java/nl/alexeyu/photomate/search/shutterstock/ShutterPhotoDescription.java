package nl.alexeyu.photomate.search.shutterstock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
  { "description" : "cats collection   vector...",
        "photo_id" : "119484406",
        "preview" : { "height" : "449",
            "url" : "http://thumb9.shutterstock.com/photos/display_pic_with_logo/856843/119484406.jpg",
            "width" : "450"
          },
        "resource_url" : "http://api.shutterstock.com/images/119484406",
        "thumb_large" : { "height" : 150,
            "url" : "http://thumb9.shutterstock.com/photos/thumb_large/856843/119484406.jpg",
            "width" : 150
          },
        "thumb_large_height" : "150",
        "thumb_large_width" : "150",
        "thumb_small" : { "height" : 100,
            "img" : "http://thumb9.shutterstock.com/photos/thumb_small/856843/119484406.jpg",
            "url" : "http://thumb9.shutterstock.com/photos/thumb_small/856843/119484406.jpg",
            "width" : 100
          },
        "thumb_small_height" : "100",
        "thumb_small_width" : "100",
        "vector_ext" : "eps",
        "web_url" : "http://www.shutterstock.com/pic.mhtml?id=119484406"
      }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShutterPhotoDescription {

    @JsonProperty("thumb_large")
    private Thumb thumb;
    
    @JsonProperty("resource_url")
    private String url;

    public String getUrl() {
        return url;
    }
    
    public String getThumbailUrl() {
        return thumb.url;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Thumb {
        @JsonProperty("url") String url;
    }

}
