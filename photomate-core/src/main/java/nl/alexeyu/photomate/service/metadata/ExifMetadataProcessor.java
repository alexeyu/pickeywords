package nl.alexeyu.photomate.service.metadata;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import com.google.common.base.Splitter;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.CmdExecutor;

@Singleton
public class ExifMetadataProcessor implements PhotoMetadataProcessor {

    private static final Splitter KEYWORD_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();

    private static final Map<PhotoProperty, Pattern> PROPERTY_PATTERN = Map.of(
		PhotoProperty.DESCRIPTION, Pattern.compile(".*(Image Description)\\s*\\:(.*)"),
		PhotoProperty.CAPTION, Pattern.compile(".*(Title)\\s*\\:(.*)"),
		PhotoProperty.KEYWORDS, Pattern.compile(".*(Keywords)\\s*\\:(.*)"),
		PhotoProperty.CREATOR, Pattern.compile(".*(Creator)\\s*\\:(.*)"));

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String TITLE = "-Title";
    private static final String CAPTION_ABSTRACT = "-Caption-Abstract";
    private static final String OBJECT_NAME = "-ObjectName";
    private static final String IMAGE_DESCRIPTION = "-ImageDescription";
    private static final String KEYWORDS = "-keywords";
    private static final String CREATOR = "-Creator";
    private static final String COPYRIGHT = "-Copyright";

    private final CmdExecutor executor;

    private final BiFunction<PhotoMetaData, PhotoMetaData, Stream<String>> changedKeywordsProvider;

    private final Consumer<Path> photoCleaner;

    private static final Map<PhotoProperty, List<String>> PHOTO_TO_EXIF_PROPERTIES = Map.of(
            PhotoProperty.CAPTION, List.of(CAPTION_ABSTRACT, OBJECT_NAME, TITLE), 
            PhotoProperty.DESCRIPTION, List.of(IMAGE_DESCRIPTION), 
            PhotoProperty.CREATOR, List.of(CREATOR, COPYRIGHT));

    public ExifMetadataProcessor(CmdExecutor executor, 
            BiFunction<PhotoMetaData, PhotoMetaData, Stream<String>> changedKeywordsProvider,
            Consumer<Path> photoCleaner) {
        this.executor = executor;
        this.changedKeywordsProvider = changedKeywordsProvider;
        this.photoCleaner = photoCleaner;
    }

    @Override
    public DefaultPhotoMetaData read(Path photoPath) {
        var arguments = List.of(IMAGE_DESCRIPTION, TITLE, CREATOR, KEYWORDS);
        var cmdOutput = executor.exec(photoPath, arguments).split(LINE_SEPARATOR);
        var properties = Stream.of(PhotoProperty.values())
                .collect(Collectors.toMap(p -> p, p -> getPhotoProperty(cmdOutput, p)));

        var builder = new DefaultPhotoMetaDataBuilder(properties);
        var keywords = preProcessKeywords(properties.get(PhotoProperty.KEYWORDS));
        builder.set(PhotoProperty.KEYWORDS, keywords);
        return builder.build();
    }

    private String getPhotoProperty(String[] cmdOutput, PhotoProperty property) {
        var pattern = PROPERTY_PATTERN.get(property);
        return Stream.of(cmdOutput)
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(m -> m.group(2).trim())
                .findFirst()
                .orElse("");
    }

    private List<String> preProcessKeywords(String keywordsLine) {
        return KEYWORD_SPLITTER.splitToList(keywordsLine.trim());
    }

    @Override
    public void update(Path photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        var arguments = getUpdateArguments(oldMetaData, newMetaData);
        if (arguments.size() > 0) {
            executor.exec(photoPath, arguments);
            photoCleaner.accept(photoPath);
        }
    }

    private Stream<String> args(PhotoMetaData newMetaData, PhotoProperty property) {
        return PHOTO_TO_EXIF_PROPERTIES.get(property).stream().map(p -> p + "=" + newMetaData.getProperty(property));
    }

    private List<String> getUpdateArguments(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        var arguments = PHOTO_TO_EXIF_PROPERTIES.keySet().stream()
                .filter(p -> !oldMetaData.getProperty(p).equals(newMetaData.getProperty(p)))
                .flatMap(p -> args(newMetaData, p));
        var changedKeywords = changedKeywordsProvider.apply(oldMetaData, newMetaData);
        return Stream.concat(arguments, changedKeywords).collect(Collectors.toList());
    }

}
