package nl.alexeyu.photomate.upload;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStockAccess;

public interface UploadNotifier {

    void notifyProgress(EditablePhoto photo, PhotoStockAccess endpoint, long bytes);

    void notifyError(EditablePhoto photo, PhotoStockAccess endpoint, Exception ex);

    void notifySuccess(EditablePhoto photo, PhotoStockAccess endpoint);

}
