package nl.alexeyu.photomate.search.shutterstock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;

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
        case CAPTION:
            return description();
        case KEYWORDS:
            return keywords();
        default:
            return "";
        }
    }

}
