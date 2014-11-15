package nl.alexeyu.photomate.service.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

@Singleton
public class ExifPhotoMetadataProcessor implements PhotoMetadataProcessor {
    
    private static final Splitter KEYWORD_SPLITTER = Splitter.on(',').trimResults();
	private static final Pattern DESCR_REGEX = Pattern.compile(".*(Image Description)\\s*\\:(.*)");
    private static final Pattern CAPTION_REGEX = Pattern.compile(".*(Caption-Abstract)\\s*\\:(.*)");
    private static final Pattern KEYWORDS_REGEX = Pattern.compile(".*(Keywords)\\s*\\:(.*)");
    private static final Pattern CREATOR_REGEX = Pattern.compile(".*(Creator)\\s*\\:(.*)");

    private static final String BACKUP_SUFFIX = "_original";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String CAPTION_ABSTRACT = "-Caption-Abstract";
    private static final String OBJECT_NAME = "-ObjectName";
    private static final String IMAGE_DESCRIPTION = "-ImageDescription";
    private static final String KEYWORDS = "-keywords";
    private static final String CREATOR = "-Creator";
    private static final String COPYRIGHT = "-Copyright";

    private static final String ADD_KEYWORD_COMMAND = KEYWORDS + "+=";
    private static final String REMOVE_KEYWORD_COMMAND = KEYWORDS + "-=";
    
    private Map<String, File> backupFilesMap = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger("ExifKeywordReader");
    
    @Override
    public DefaultPhotoMetaData read(Path photoPath) {
        String[] photoProperties = 
                execExif(photoPath.toString(), IMAGE_DESCRIPTION, CAPTION_ABSTRACT, CREATOR, KEYWORDS)
                .split(LINE_SEPARATOR);
        Map<PhotoProperty, Object> properties = new HashMap<>();
        properties.put(PhotoProperty.CAPTION, getPhotoProperty(photoProperties, CAPTION_REGEX));
        properties.put(PhotoProperty.DESCRIPTION, getPhotoProperty(photoProperties, DESCR_REGEX));
        properties.put(PhotoProperty.CREATOR, getPhotoProperty(photoProperties, CREATOR_REGEX));
        properties.put(PhotoProperty.KEYWORDS, preProcessKeywords(getPhotoProperty(photoProperties, KEYWORDS_REGEX)));
        return new DefaultPhotoMetaData(properties);
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
        return Lists.newArrayList(KEYWORD_SPLITTER.split(keywordsLine));
    }

    @Override
    public void update(Path photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = getUpdateArguments(oldMetaData, newMetaData);
        if (arguments.size() > 0) {
            execExif(photoPath.toString(), arguments.toArray(new String[arguments.size()]));
            ensureBackupDeleted(photoPath);
        }
    }

	private void ensureBackupDeleted(Path photoPath) {
		String backupFilePath = photoPath.toString() + BACKUP_SUFFIX;
		File backupFile = new File(backupFilePath);
		backupFile.deleteOnExit();
		backupFilesMap.put(backupFilePath, backupFile);
	}

	private List<String> args(PhotoMetaData oldMetaData, PhotoMetaData newMetaData,
			PhotoProperty property, String... exifProperties) {
		if (oldMetaData.getProperty(property).equals(newMetaData.getProperty(property))) {
			return Collections.emptyList();
	    }
		List<String> result = new ArrayList<>();
		for (String exifProp : exifProperties) {
			result.add(exifProp + "=" + newMetaData.getProperty(property));
		}
		return result;
	}

	private List<String> getUpdateArguments(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(args(oldMetaData, newMetaData, PhotoProperty.CAPTION, CAPTION_ABSTRACT, OBJECT_NAME));
        arguments.addAll(args(oldMetaData, newMetaData, PhotoProperty.DESCRIPTION, IMAGE_DESCRIPTION));
        arguments.addAll(args(oldMetaData, newMetaData, PhotoProperty.CREATOR, CREATOR, COPYRIGHT));

        Collection<String> oldKeywords = oldMetaData.getKeywords();
        Collection<String> newKeywords = newMetaData.getKeywords();
        if (!oldKeywords.equals(newKeywords)) {
        	Collection<String> addedKeywords = newKeywords.stream()
        			.filter(k -> !oldKeywords.contains(k))
        			.collect(Collectors.toList());
            arguments.addAll(toArgs(addedKeywords, true));
            Collection<String> removedKeywords = oldKeywords.stream()
            		.filter(k -> !newKeywords.contains(k))
            		.collect(Collectors.toList());
            arguments.addAll(toArgs(removedKeywords, false));
        }
        return arguments;
    }
    
    private List<String> toArgs(Collection<String> keywords, boolean add) {
    	String command = add ? ADD_KEYWORD_COMMAND : REMOVE_KEYWORD_COMMAND;
        return keywords.stream()
        		.map(kw -> command + kw.trim())
        		.collect(Collectors.toList());
    }

    private String execExif(String path, String... args) {
        try {
            String[] execArgs = new String[args.length + 2];
            execArgs[0] = "exiftool";
            System.arraycopy(args, 0, execArgs, 1, args.length);
            execArgs[execArgs.length - 1] = path;
            logger.debug("" + Arrays.asList(execArgs));
            return doExec(execArgs);
        } catch (IOException ex) {
            logger.error("Could not run exif", ex);
            return "";
        }
    }

	private String doExec(String[] execArgs) throws IOException {
		Process p = Runtime.getRuntime().exec(execArgs);
		try (InputStream is = p.getInputStream()) {
		    String result = CharStreams.toString(new InputStreamReader(is));
		    logger.debug(result);
		    return result;
		}
	}

}
