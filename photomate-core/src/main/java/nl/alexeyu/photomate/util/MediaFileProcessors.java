package nl.alexeyu.photomate.util;

public interface MediaFileProcessors {

    ExtensionBasedMediaFilesProcessor JPEG = new ExtensionBasedMediaFilesProcessor(".jpg", ".jpeg");

    ExtensionBasedMediaFilesProcessor MPEG4 = new ExtensionBasedMediaFilesProcessor(".mp4", ".mpeg");

}
