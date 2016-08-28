package nl.alexeyu.photomate.service.upload;

public interface UploadNotifier {

    void notifyProgress(UploadAttempt uploadAttempt, long bytes);

    void notifyError(UploadAttempt uploadAttempt, Exception ex);

    void notifySuccess(UploadAttempt uploadAttempt);

}
