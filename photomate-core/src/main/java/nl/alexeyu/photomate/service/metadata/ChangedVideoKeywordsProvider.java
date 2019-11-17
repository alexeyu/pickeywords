package nl.alexeyu.photomate.service.metadata;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.google.common.base.Joiner;

import nl.alexeyu.photomate.model.PhotoMetaData;

public final class ChangedVideoKeywordsProvider implements BiFunction<PhotoMetaData, PhotoMetaData, List<String>> {

    private static final Joiner KEYWORD_JOINER = Joiner.on(", ");

    @Override
    public List<String> apply(PhotoMetaData oldMetaData, PhotoMetaData newMetaData) {
        if (!newMetaData.keywords().equals(oldMetaData.keywords())) {
            return List.of(String.format("-keywords=%s", KEYWORD_JOINER.join(newMetaData.keywords())));
        }
        return List.of();
    }

}
