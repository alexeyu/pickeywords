package nl.alexeyu.photomate.service.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.util.CmdExecutor;

@RunWith(MockitoJUnitRunner.class)
public class ExifPhotoMetadataProcessorTest {

    private static final List<String> DEFAULT_RESPONSE = Arrays.asList("Image Description               : Coolpic",
            "Caption-Abstract:My Photo", "Creator        : Me", "Keywords  : landscape, evening, summer");

    private static final Joiner JOINER = Joiner.on(System.getProperty("line.separator"));

    private ExifPhotoMetadataProcessor processor;

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
        processor = new ExifPhotoMetadataProcessor(executor, backupCleaner);
        pathCaptor = ArgumentCaptor.forClass(Path.class);
        argsCaptor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void read() {
        when(executor.exec(Mockito.any(Path.class), Mockito.anyList())).thenReturn(JOINER.join(DEFAULT_RESPONSE));

        PhotoMetaData metaData = processor.read(testPath);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verifyZeroInteractions(backupCleaner);
        assertEquals(testPath, pathCaptor.getValue());
        assertEquals(Sets.newHashSet("-ImageDescription", "-Caption-Abstract", "-Creator", "-keywords"),
                Sets.newHashSet(argsCaptor.getValue()));

        assertEquals("Coolpic", metaData.description());
        assertEquals("My Photo", metaData.caption());
        assertEquals("Me", metaData.getProperty(PhotoProperty.CREATOR));
        assertEquals(Arrays.asList("landscape", "evening", "summer"), metaData.keywords());
    }

    @Test
    public void readEmptyResponse() {
        when(executor.exec(Mockito.eq(testPath), Mockito.anyList())).thenReturn("");
        verifyZeroInteractions(backupCleaner);
        PhotoMetaData metaData = processor.read(testPath);

        assertEquals("", metaData.description());
        assertEquals("", metaData.caption());
        assertEquals("", metaData.getProperty(PhotoProperty.CREATOR));
        assertTrue(metaData.keywords().isEmpty());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void changeCaption() {
        PhotoMetaData oldMetaData = new DefaultPhotoMetaDataBuilder().build();
        PhotoMetaData newMetaData = new DefaultPhotoMetaDataBuilder().set(PhotoProperty.CAPTION, "New Caption").build();
        processor.update(testPath, oldMetaData, newMetaData);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verify(backupCleaner).accept(pathCaptor.getValue());
        assertEquals(testPath, pathCaptor.getValue());

        List<String> args = argsCaptor.getValue();
        assertEquals(2, args.size());
        assertTrue(args.contains("-Caption-Abstract=New Caption"));
        assertTrue(args.contains("-ObjectName=New Caption"));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void changeCreator() {
        PhotoMetaData oldMetaData = new DefaultPhotoMetaDataBuilder().build();
        PhotoMetaData newMetaData = new DefaultPhotoMetaDataBuilder().set(PhotoProperty.CREATOR, "Me").build();
        processor.update(testPath, oldMetaData, newMetaData);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verify(backupCleaner).accept(pathCaptor.getValue());
        assertEquals(testPath, pathCaptor.getValue());

        List<String> args = argsCaptor.getValue();
        assertEquals(2, args.size());
        assertTrue(args.contains("-Creator=Me"));
        assertTrue(args.contains("-Copyright=Me"));
    }

    @Test
    public void dontCallExifIfMetadataNotChanged() {
        PhotoMetaData oldMetaData = new DefaultPhotoMetaDataBuilder().set(PhotoProperty.CAPTION, "Caption").build();
        PhotoMetaData newMetaData = new DefaultPhotoMetaDataBuilder().set(PhotoProperty.CAPTION, "Caption").build();
        processor.update(testPath, oldMetaData, newMetaData);
        verifyZeroInteractions(executor);
        verifyZeroInteractions(backupCleaner);
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void changeKeywords() {
        PhotoMetaData oldMetaData = new DefaultPhotoMetaDataBuilder()
                .set(PhotoProperty.KEYWORDS, Arrays.asList("summer", "evening")).build();
        PhotoMetaData newMetaData = new DefaultPhotoMetaDataBuilder()
                .set(PhotoProperty.KEYWORDS, Arrays.asList("landscape", "summer")).build();
        processor.update(testPath, oldMetaData, newMetaData);

        verify(executor).exec(pathCaptor.capture(), argsCaptor.capture());
        verify(backupCleaner).accept(pathCaptor.getValue());
        assertEquals(testPath, pathCaptor.getValue());

        List<String> args = argsCaptor.getValue();
        assertEquals(2, args.size());
        assertTrue(args.contains("-keywords+=landscape"));
        assertTrue(args.contains("-keywords-=evening"));
    }
}
