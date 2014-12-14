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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

@Singleton
public class ExifPhotoMetadataProcessor implements PhotoMetadataProcessor {
    
    private static final Splitter KEYWORD_SPLITTER = Splitter.on(',').trimResults();
    
    private static final Map<PhotoProperty, Pattern> PROPERTY_PATTERN = ImmutableMap.of(
    	PhotoProperty.DESCRIPTION, Pattern.compile(".*(Image Description)\\s*\\:(.*)"),
    	PhotoProperty.CAPTION, Pattern.compile(".*(Caption-Abstract)\\s*\\:(.*)"),
    	PhotoProperty.KEYWORDS, Pattern.compile(".*(Keywords)\\s*\\:(.*)"),
    	PhotoProperty.CREATOR, Pattern.compile(".*(Creator)\\s*\\:(.*)"));
    

    private static final String BACKUP_SUFFIX = "_original";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String CAPTION_ABSTRACT = "-Caption-Abstract";
    private static final String OBJECT_NAME = "-ObjectName";
    private static final String IMAGE_DESCRIPTION = "-ImageDescription";
    private static final String KEYWORDS = "-keywords";
    private static final String CREATOR = "-Creator";
    private static final String COPYRIGHT = "-Copyright";
    

    private static final Map<PhotoProperty, List<String>> PHOTO_TO_EXIF_PROPERTIES = ImmutableMap.of(
    		PhotoProperty.CAPTION, Arrays.asList(CAPTION_ABSTRACT, OBJECT_NAME),
    		PhotoProperty.DESCRIPTION, Arrays.asList(IMAGE_DESCRIPTION),
    		PhotoProperty.CREATOR, Arrays.asList(CREATOR, COPYRIGHT));

    private static final String ADD_KEYWORD_COMMAND = KEYWORDS + "+=";
    private static final String REMOVE_KEYWORD_COMMAND = KEYWORDS + "-=";
    
    private Map<String, File> backupFilesMap = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger("ExifKeywordReader");
    
    @Override
    public DefaultPhotoMetaData read(Path photoPath) {
        List<String> photoProperties = Arrays.asList(
        	execExif(photoPath, IMAGE_DESCRIPTION, CAPTION_ABSTRACT, CREATOR, KEYWORDS)
            .split(LINE_SEPARATOR));
        Map<PhotoProperty, Object> properties = Stream.of(PhotoProperty.values())
        		.collect(Collectors.toMap(
        				p -> p, 
        				p -> getPhotoProperty(photoProperties)));
        properties.put(PhotoProperty.KEYWORDS, preProcessKeywords(properties.get(PhotoProperty.KEYWORDS).toString()));
        return new DefaultPhotoMetaData(properties);
    }
    
    private String getPhotoProperty(List<String> photoProperties) {
    	return photoProperties.stream()
    		.map(p -> PROPERTY_PATTERN.get(p).matcher(p))
    		.filter(m -> m.matches())
    		.map(m -> m.group(2).trim())
    		.findFirst()
    		.orElse("");
    }
    
    private List<String> preProcessKeywords(String keywordsLine) {
        return Lists.newArrayList(KEYWORD_SPLITTER.split(keywordsLine));
    }

    @Override
    public void update(Path photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = getUpdateArguments(oldMetaData, newMetaData);
        if (arguments.size() > 0) {
            execExif(photoPath, arguments.toArray(new String[arguments.size()]));
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
			PhotoProperty property) {
		if (oldMetaData.getProperty(property).equals(newMetaData.getProperty(property))) {
			return Collections.emptyList();
	    }
		return PHOTO_TO_EXIF_PROPERTIES.get(property).stream()
				.map(p -> p + "=" + newMetaData.getProperty(property))
				.collect(Collectors.toList());
	}

	private List<String> getUpdateArguments(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = new ArrayList<>();
        PHOTO_TO_EXIF_PROPERTIES.keySet().stream().forEach(p ->
        	arguments.addAll(args(oldMetaData, newMetaData, p)));

    	Stream<String> changedKeywords = Stream.concat(
    		diff(newMetaData.keywords(), oldMetaData.keywords())
    			.map(kw -> ADD_KEYWORD_COMMAND + kw.trim()),
    		diff(oldMetaData.keywords(), newMetaData.keywords())
        		.map(kw -> REMOVE_KEYWORD_COMMAND + kw.trim()));
        arguments.addAll(changedKeywords.collect(Collectors.toList()));

        return arguments;
    }
	
	private Stream<String> diff(Collection<String> a, Collection<String> b) {
		return a.stream().filter(element -> !b.contains(element));
	}
    
    private String execExif(Path path, String... args) {
        try {
            String[] execArgs = new String[args.length + 2];
            execArgs[0] = "exiftool";
            System.arraycopy(args, 0, execArgs, 1, args.length);
            execArgs[execArgs.length - 1] = path.toString();
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
