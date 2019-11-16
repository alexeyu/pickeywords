package nl.alexeyu.photomate.service.metadata;

import static nl.alexeyu.photomate.model.PhotoProperty.CAPTION;
import static nl.alexeyu.photomate.model.PhotoProperty.CREATOR;
import static nl.alexeyu.photomate.model.PhotoProperty.KEYWORDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import nl.alexeyu.photomate.model.DefaultPhotoMetaDataBuilder;
import nl.alexeyu.photomate.util.CmdExecutor;

@RunWith(MockitoJUnitRunner.class)
public class ExifPhotoMetadataProcessorTest {

    private static final List<String> DEFAULT_RESPONSE = List.of(
            "Image Description               : Coolpic",
            "Title:My Photo", "Creator        : Me",
            "Keywords  : landscape, evening, summer");

    private static final Joiner JOINER = Joiner.on(System.getProperty("line.separator"));

    private ExifMetadataProcessor processor;

    private CmdExecutor executor;

    private Path testPath = Paths.get(".");

    private Consumer<Path> backupCleaner;

    private ArgumentCaptor<Path> pathCaptor;

    @SuppressWarnings({ "rawtypes" })
    private ArgumentCaptor<List> argsCaptor;

    @Before
    @SuppressWarnings({ "unchecked" })
    public void setUp() {
        executor = mock(CmdExecutor.class);
        backupCleaner = mock(Consumer.class);
        processor = new ExifMetadataProcessor(executor, new ChangedVideoKeywordsProvider(), backupCleaner);
        pathCaptor = ArgumentCaptor.forClass(Path.class);
        argsCaptor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void read() {
        when(executor.exec(Mockito.any(Path.class), Mockito.anyList())).thenReturn(JOINER.join(DEFAULT_RESPONSE));

        var metaData = processor.read(testPath);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verifyZeroInteractions(backupCleaner);
        assertEquals(testPath, pathCaptor.getValue());
        assertEquals(Sets.newHashSet("-ImageDescription", "-Title", "-Creator", "-keywords"),
                Sets.newHashSet(argsCaptor.getValue()));

        assertEquals("Coolpic", metaData.description());
        assertEquals("My Photo", metaData.caption());
        assertEquals("Me", metaData.getProperty(CREATOR));
        assertEquals(List.of("landscape", "evening", "summer"), metaData.keywords());
    }

    @Test
    public void readEmptyResponse() {
        when(executor.exec(Mockito.eq(testPath), Mockito.anyList())).thenReturn("");
        verifyZeroInteractions(backupCleaner);
        var metaData = processor.read(testPath);

        assertEquals("", metaData.description());
        assertEquals("", metaData.caption());
        assertEquals("", metaData.getProperty(CREATOR));
        assertTrue(metaData.keywords().isEmpty());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void changeCaption() {
        var oldMetaData = new DefaultPhotoMetaDataBuilder().build();
        var newMetaData = new DefaultPhotoMetaDataBuilder().set(CAPTION, "New Caption").build();
        processor.update(testPath, oldMetaData, newMetaData);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verify(backupCleaner).accept(pathCaptor.getValue());
        assertEquals(testPath, pathCaptor.getValue());

        var args = argsCaptor.getValue();
        assertEquals(3, args.size());
        assertTrue(args.contains("-Caption-Abstract=New Caption"));
        assertTrue(args.contains("-ObjectName=New Caption"));
        assertTrue(args.contains("-Title=New Caption"));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void changeCreator() {
        var oldMetaData = new DefaultPhotoMetaDataBuilder().build();
        var newMetaData = new DefaultPhotoMetaDataBuilder().set(CREATOR, "Me").build();
        processor.update(testPath, oldMetaData, newMetaData);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verify(backupCleaner).accept(pathCaptor.getValue());
        assertEquals(testPath, pathCaptor.getValue());

        var args = argsCaptor.getValue();
        assertEquals(2, args.size());
        assertTrue(args.contains("-Creator=Me"));
        assertTrue(args.contains("-Copyright=Me"));
    }

    @Test
    public void dontCallExifIfMetadataNotChanged() {
        var oldMetaData = new DefaultPhotoMetaDataBuilder().set(CAPTION, "Caption").build();
        var newMetaData = new DefaultPhotoMetaDataBuilder().set(CAPTION, "Caption").build();
        processor.update(testPath, oldMetaData, newMetaData);
        verifyZeroInteractions(executor);
        verifyZeroInteractions(backupCleaner);
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void changeKeywords() {
        var oldMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("summer", "evening")).build();
        var newMetaData = new DefaultPhotoMetaDataBuilder().set(KEYWORDS, List.of("landscape", "summer")).build();
        processor.update(testPath, oldMetaData, newMetaData);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verify(backupCleaner).accept(pathCaptor.getValue());
        assertEquals(testPath, pathCaptor.getValue());

        var args = argsCaptor.getValue();
        assertEquals(1, args.size());
        assertEquals("-keywords=landscape, summer", args.get(0));
    }

}
