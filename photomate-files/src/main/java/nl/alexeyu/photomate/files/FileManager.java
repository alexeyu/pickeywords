package nl.alexeyu.photomate.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileManager {

    private static final Logger logger = LogManager.getLogger();

    public static List<Path> move(List<Path> files, Path to) {
        createDirectory(to);
        List<Path> failedFiles = new ArrayList<>();
        for (Path file : files) {
            try {
                Files.move(file, to.resolve(file.getFileName()));
            } catch (IOException ex) {
                failedFiles.add(file);
                logger.error("Could not copy file {} to {}", file, to, ex);
            }
        }
        return failedFiles;
    }

    public static List<Path> copy(List<Path> files, Path to) {
        createDirectory(to);
        List<Path> failedFiles = new ArrayList<>();
        for (Path file : files) {
            try {
                Files.copy(file, to.resolve(file.getFileName()));
            } catch (IOException ex) {
                failedFiles.add(file);
                logger.error("Could not move file {} to {}", file, to, ex);
            }
        }
        return failedFiles;
    }

    private static void createDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException ex) {
            logger.error("Could not create directory " + dir);
        }
    }
}
