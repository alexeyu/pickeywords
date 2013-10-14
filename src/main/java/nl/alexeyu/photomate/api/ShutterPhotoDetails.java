package nl.alexeyu.photomate.api;

import java.io.File;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

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
public class ShutterPhotoDetails {

    @JsonProperty("keywords")
    private List<String> keywords;

    public List<String> getKeywords() {
        return keywords;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new ObjectMapper().readValue(new File("/home/lesha/img.json"), ShutterPhotoDetails.class).getKeywords());
    }
}
