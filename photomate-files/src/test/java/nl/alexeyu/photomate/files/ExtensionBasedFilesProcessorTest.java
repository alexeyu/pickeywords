package nl.alexeyu.photomate.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class ExtensionBasedFilesProcessorTest {

    @Test
    public void filtersByMultipleExtesions() {
        ExtensionBasedFilesProcessor processor = new ExtensionBasedFilesProcessor(".one", ".two");
        assertTrue(processor.test(Paths.get("tmp", "file.one")));
        assertTrue(processor.test(Paths.get("tmp", "file.two")));
        assertFalse(processor.test(Paths.get("tmp")));
        assertFalse(processor.test(Paths.get("tmp", "fileone")));
    }

    @Test
    public void getsAllNecessaryFilesFromDir() throws IOException {
        Path dir = prepareFiles();
        ExtensionBasedFilesProcessor processor = new ExtensionBasedFilesProcessor(".one", ".two");
        List<Path> filteredPaths = processor.apply(dir).collect(Collectors.toList());
        assertEquals(2, filteredPaths.size());
        assertEquals(dir.resolve("file.one"), filteredPaths.get(0));
        assertEquals(dir.resolve("file.two"), filteredPaths.get(1));
    }

    @Test(expected = IllegalStateException.class)
    public void throwsISEInCaseOfIOException() throws IOException {
        ExtensionBasedFilesProcessor processor = new ExtensionBasedFilesProcessor(".one", ".two");
        processor.apply(Paths.get("doesnotexist"));
    }

    private Path prepareFiles() throws IOException {
        Path tempDir = Files.createTempDirectory("photomate-test");
        Files.createFile(tempDir.resolve("file.one"));
        Files.createFile(tempDir.resolve("file.two"));
        Files.createFile(tempDir.resolve("file.three"));
        return tempDir;
    }
}
