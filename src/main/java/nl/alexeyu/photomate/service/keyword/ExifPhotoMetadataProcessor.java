package nl.alexeyu.photomate.service.keyword;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.PhotoMetaData;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExifPhotoMetadataProcessor implements PhotoMetadataProcessor {
    
    private static final Pattern DESCR_REGEX = Pattern.compile(".*(Image Description)\\s*\\:(.*)");
    private static final Pattern CAPTION_REGEX = Pattern.compile(".*(Caption-Abstract)\\s*\\:(.*)");
    private static final Pattern KEYWORDS_REGEX = Pattern.compile(".*(Keywords)\\s*\\:(.*)");
    private static final Pattern CREATOR_REGEX = Pattern.compile(".*(Creator)\\s*\\:(.*)");

    private static final String CAPTION_ABSTRACT = "-Caption-Abstract";
    private static final String IMAGE_DESCRIPTION = "-ImageDescription";
    private static final String KEYWORDS = "-keywords";
    private static final String CREATOR = "-Creator";
    private static final String COPYRIGHT = "-Copyright";

    private static final String ADD_KEYWORD_COMMAND = KEYWORDS + "+=";
    private static final String REMOVE_KEYWORD_COMMAND = KEYWORDS + "-=";

    private final Logger logger = LoggerFactory.getLogger("ExifKeywordReader");
    
    @Override
    public DefaultPhotoMetaData read(String photoUrl) {
        String[] photoProperties = 
                execExif(photoUrl, IMAGE_DESCRIPTION, CAPTION_ABSTRACT, CREATOR, KEYWORDS)
                .split("\n");
        return new DefaultPhotoMetaData(
                getPhotoProperty(photoProperties, CAPTION_REGEX),
                getPhotoProperty(photoProperties, DESCR_REGEX),
                getPhotoProperty(photoProperties, CREATOR_REGEX),
                preProcessKeywords(getPhotoProperty(photoProperties, KEYWORDS_REGEX)));
    }
    
    private String getPhotoProperty(String[] photoProperties, Pattern propertyPattern) {
        for (String line : photoProperties) {
            Matcher m = propertyPattern.matcher(line);
            if (m.matches()) {
                return m.group(2).trim();
            }
        }
        return "";
    }
    
    private List<String> preProcessKeywords(String keywordsLine) {
        List<String> keywords = new ArrayList<>();
        for (String keyword : StringUtils.split(keywordsLine, ",")) {
            keywords.add(keyword.trim());
        }
        return keywords;
    }

    @Override
    public void update(String photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = getUpdateArguments(oldMetaData, newMetaData);
        if (arguments.size() > 0) {
            execExif(photoPath, arguments.toArray(new String[arguments.size()]));
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getUpdateArguments(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = new ArrayList<>();
        if (!oldMetaData.getCaption().trim().equals(newMetaData.getCaption().trim())) {
            arguments.add(CAPTION_ABSTRACT + "=" + newMetaData.getCaption().trim());
        }
        if (!oldMetaData.getDescription().trim().equals(newMetaData.getDescription().trim())) {
            arguments.add(IMAGE_DESCRIPTION + "=" + newMetaData.getDescription().trim());
        }
        if (!oldMetaData.getCreator().trim().equals(newMetaData.getCreator().trim())) {
            arguments.add(CREATOR + "=" + newMetaData.getCreator().trim());
            arguments.add(COPYRIGHT + "=" + newMetaData.getCreator().trim());
        }

        List<String> oldKeywords = new ArrayList<>(oldMetaData.getKeywords());
        List<String> newKeywords = new ArrayList<>(newMetaData.getKeywords());
        if (!oldKeywords.equals(newKeywords)) {
            Collection<String> addedKeywords = ListUtils.removeAll(newKeywords, oldKeywords);
            arguments.addAll(toArgs(addedKeywords, true));
            Collection<String> removedKeywords = ListUtils.removeAll(oldKeywords, newKeywords);
            arguments.addAll(toArgs(removedKeywords, false));
        }
        return arguments;
    }
    
    private List<String> toArgs(Collection<String> keywords, boolean add) {
        List<String> result = new ArrayList<String>(keywords.size());
        for (String kw : keywords) {
            result.add((add ? ADD_KEYWORD_COMMAND : REMOVE_KEYWORD_COMMAND) + kw.trim());
        }
        return result;
    }

    private String execExif(String path, String... args) {
        try {
            String[] execArgs = new String[args.length + 2];
            execArgs[0] = "exiftool";
            System.arraycopy(args, 0, execArgs, 1, args.length);
            execArgs[execArgs.length - 1] = path;
            logger.debug("" + Arrays.asList(execArgs));
            Process p = Runtime.getRuntime().exec(execArgs);
            try (InputStream is = p.getInputStream()) {
                String result = IOUtils.toString(is);
                logger.debug(result);
                return result;
            }
        } catch (IOException ex) {
            logger.error("Could not run exif", ex);
            return "";
        }
    }

}
