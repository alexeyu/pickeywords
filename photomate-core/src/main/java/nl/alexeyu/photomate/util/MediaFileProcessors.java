package nl.alexeyu.photomate.util;

import nl.alexeyu.photomate.files.ExtensionBasedFilesProcessor;

public interface MediaFileProcessors {

    ExtensionBasedFilesProcessor JPEG = new ExtensionBasedFilesProcessor(".jpg", ".jpeg");

    ExtensionBasedFilesProcessor MPEG4 = new ExtensionBasedFilesProcessor(".mp4", ".mpeg");

}
