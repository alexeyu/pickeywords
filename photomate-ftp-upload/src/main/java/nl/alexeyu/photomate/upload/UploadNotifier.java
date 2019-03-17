package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public interface UploadNotifier {

    void notifyProgress(EditablePhoto photo, FtpEndpoint endpoint, long bytes);

    void notifyError(EditablePhoto photo, FtpEndpoint endpoint, Exception ex);

    void notifySuccess(EditablePhoto photo, FtpEndpoint endpoint);

}
