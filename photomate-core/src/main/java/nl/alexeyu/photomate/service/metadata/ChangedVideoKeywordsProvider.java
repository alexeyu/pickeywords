package nl.alexeyu.photomate.service.metadata;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.google.common.base.Joiner;

import nl.alexeyu.photomate.model.PhotoMetaData;

public final class ChangedVideoKeywordsProvider implements BiFunction<PhotoMetaData, PhotoMetaData, Stream<String>> {

    private static final Joiner KEYWORD_JOINER = Joiner.on(", ");

    @Override
    public Stream<String> apply(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        if (!newMetaData.keywords().equals(oldMetaData.keywords())) {
            return Stream.of(String.format("-keywords=%s", KEYWORD_JOINER.join(newMetaData.keywords())));
        }
        return Stream.of();
    }

}
