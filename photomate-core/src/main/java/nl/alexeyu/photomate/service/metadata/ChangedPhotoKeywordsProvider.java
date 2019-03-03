package nl.alexeyu.photomate.service.metadata;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import nl.alexeyu.photomate.model.PhotoMetaData;

public final class ChangedPhotoKeywordsProvider implements BiFunction<PhotoMetaData, PhotoMetaData, Stream<String>> {
    
    private static final String ADD_KEYWORD_COMMAND = "-keywords+=";
    private static final String REMOVE_KEYWORD_COMMAND = "-keywords-=";

    @Override
    public Stream<String> apply(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        return Stream.concat(
                diff(newMetaData.keywords(), oldMetaData.keywords()).map(kw -> ADD_KEYWORD_COMMAND + kw.trim()),
                diff(oldMetaData.keywords(), newMetaData.keywords()).map(kw -> REMOVE_KEYWORD_COMMAND + kw.trim()));
    }

    private Stream<String> diff(Collection<String> a, Collection<String> b) {
        return a.stream().filter(element -> !b.contains(element));
    }

}
