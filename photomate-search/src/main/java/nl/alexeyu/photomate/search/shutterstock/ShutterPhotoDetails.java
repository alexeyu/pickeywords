package nl.alexeyu.photomate.search.shutterstock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;

/*{
   "illustration":1,
   "r_rated":"0",
   "photo_id":"119484406",
   "enhanced_license_available":"1",
   "resource_url":"http://api.shutterstock.com/images/119484406",
   "categories":[
      {
         "category_id":"1",
         "category":"Animals/Wildlife"
      },
      {
         "category_id":"17",
         "category":"Signs/Symbols"
      }
   ],
   "model_release":{
      "translation_id":"N_A",
      "code":"0"
   },
   "vector_type":"eps",
   "description":"Cats collection - vector silhouette",
   "sizes":{
      "small":{
         "width":"500",
         "height":"500",
         "display_name":"Small"
      },
      "preview":{
         "url":"http://thumb9.shutterstock.com/photos/display_pic_with_logo/856843/119484406.jpg"
      },
      "medium":{
         "width":"1000",
         "height":"999",
         "display_name":"Medium"
      },
      "thumb_large":{
         "width":"150",
         "url":"http://thumb9.shutterstock.com/photos/thumb_large/856843/119484406.jpg",
         "height":"150"
      },
      "thumb_small":{
         "width":"100",
         "url":"http://thumb9.shutterstock.com/photos/thumb_small/856843/119484406.jpg",
         "img":"http://thumb9.shutterstock.com/photos/thumb_small/856843/119484406.jpg",
         "height":"100"
      },
      "huge":{
         "width":"800",
         "height":"799",
         "display_name":"Large"
      }
   },
   "keywords":[],
   "is_vector":1,
   "web_url":"http://www.shutterstock.com/pic.mhtml?id=119484406",
   "submitter_id":"856843",
   "submitter":"Hein Nouwens"
} 
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShutterPhotoDetails implements PhotoMetaData {

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonProperty("description")
    private String description;

    @Override
    public List<String> keywords() {
        return keywords == null ? List.of() : keywords;
    }

    @Override
    public String caption() {
        return description;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Object getProperty(PhotoProperty p) {
        switch (p) {
        case DESCRIPTION:
            return description();
        case KEYWORDS:
            return keywords();
        default:
            return "";
        }
    }

}
