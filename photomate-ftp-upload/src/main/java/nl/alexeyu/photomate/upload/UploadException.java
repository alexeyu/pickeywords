package nl.alexeyu.photomate.upload;

public class UploadException extends RuntimeException {

    public UploadException() {
    }

    public UploadException(Exception cause) {
        super(cause);
    }

}
