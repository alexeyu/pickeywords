package nl.alexeyu.photomate.service.metadata;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.CmdExecutor;

@Singleton
public class ExifPhotoMetadataProcessor implements PhotoMetadataProcessor {

    private static final Splitter KEYWORD_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();

    private static final Map<PhotoProperty, Pattern> PROPERTY_PATTERN = ImmutableMap.of(PhotoProperty.DESCRIPTION,
            Pattern.compile(".*(Image Description)\\s*\\:(.*)"), PhotoProperty.CAPTION,
            Pattern.compile(".*(Caption-Abstract)\\s*\\:(.*)"), PhotoProperty.KEYWORDS,
            Pattern.compile(".*(Keywords)\\s*\\:(.*)"), PhotoProperty.CREATOR,
            Pattern.compile(".*(Creator)\\s*\\:(.*)"));

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String CAPTION_ABSTRACT = "-Caption-Abstract";
    private static final String OBJECT_NAME = "-ObjectName";
    private static final String IMAGE_DESCRIPTION = "-ImageDescription";
    private static final String KEYWORDS = "-keywords";
    private static final String CREATOR = "-Creator";
    private static final String COPYRIGHT = "-Copyright";

    private final CmdExecutor executor;

    private final Consumer<Path> photoCleaner;

    private static final Map<PhotoProperty, List<String>> PHOTO_TO_EXIF_PROPERTIES = ImmutableMap.of(
            PhotoProperty.CAPTION, Arrays.asList(CAPTION_ABSTRACT, OBJECT_NAME), PhotoProperty.DESCRIPTION,
            Arrays.asList(IMAGE_DESCRIPTION), PhotoProperty.CREATOR, Arrays.asList(CREATOR, COPYRIGHT));

    private static final String ADD_KEYWORD_COMMAND = KEYWORDS + "+=";
    private static final String REMOVE_KEYWORD_COMMAND = KEYWORDS + "-=";

    public ExifPhotoMetadataProcessor(CmdExecutor executor, Consumer<Path> photoCleaner) {
        this.executor = executor;
        this.photoCleaner = photoCleaner;
    }

    @Override
    public DefaultPhotoMetaData read(Path photoPath) {
        List<String> arguments = Arrays.asList(IMAGE_DESCRIPTION, CAPTION_ABSTRACT, CREATOR, KEYWORDS);
        String[] cmdOutput = executor.exec(photoPath, arguments).split(LINE_SEPARATOR);
        Map<PhotoProperty, String> properties = Stream.of(PhotoProperty.values())
                .collect(Collectors.toMap(p -> p, p -> getPhotoProperty(cmdOutput, p)));

        DefaultPhotoMetaDataBuilder builder = new DefaultPhotoMetaDataBuilder(properties);
        List<String> keywords = preProcessKeywords(properties.get(PhotoProperty.KEYWORDS));
        builder.set(PhotoProperty.KEYWORDS, keywords);
        return builder.build();
    }

    private String getPhotoProperty(String[] cmdOutput, PhotoProperty property) {
        Pattern pattern = PROPERTY_PATTERN.get(property);
        return Stream.of(cmdOutput).map(line -> pattern.matcher(line)).filter(m -> m.matches())
                .map(m -> m.group(2).trim()).findFirst().orElse("");
    }

    private List<String> preProcessKeywords(String keywordsLine) {
        return Lists.newArrayList(KEYWORD_SPLITTER.split(keywordsLine.trim()));
    }

    @Override
    public void update(Path photoPath, PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        List<String> arguments = getUpdateArguments(oldMetaData, newMetaData);
        if (arguments.size() > 0) {
            executor.exec(photoPath, arguments);
            photoCleaner.accept(photoPath);
        }
    }

    private Stream<String> args(PhotoMetaData newMetaData, PhotoProperty property) {
        return PHOTO_TO_EXIF_PROPERTIES.get(property).stream().map(p -> p + "=" + newMetaData.getProperty(property));
    }

    private List<String> getUpdateArguments(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        Stream<String> arguments = PHOTO_TO_EXIF_PROPERTIES.keySet().stream()
                .filter(p -> !oldMetaData.getProperty(p).equals(newMetaData.getProperty(p)))
                .flatMap(p -> args(newMetaData, p));

        Stream<String> changedKeywords = Stream.concat(
                diff(newMetaData.keywords(), oldMetaData.keywords()).map(kw -> ADD_KEYWORD_COMMAND + kw.trim()),
                diff(oldMetaData.keywords(), newMetaData.keywords()).map(kw -> REMOVE_KEYWORD_COMMAND + kw.trim()));
        return Stream.concat(arguments, changedKeywords).collect(Collectors.toList());
    }

    private Stream<String> diff(Collection<String> a, Collection<String> b) {
        return a.stream().filter(element -> !b.contains(element));
    }

}
