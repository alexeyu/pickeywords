package nl.alexeyu.photomate.ui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadTableModel extends AbstractTableModel {

    private final List<EditablePhoto> photos;

    private final List<PhotoStock> photoStocks;

    private final Map<String, Object> statuses = new ConcurrentHashMap<>();

    public UploadTableModel(List<EditablePhoto> photos, List<PhotoStock> photoStocks) {
        this.photos = photos;
        this.photoStocks = photoStocks;
    }

    public void setStatus(FtpEndpoint endpoint, Photo photo, Object status) {
        var key = getKey(endpoint, photo);
        statuses.put(key, status);
    }

    private String getKey(FtpEndpoint endpoint, Photo photo) {
        return photo.name() + "-" + endpoint.url();
    }

    public int getRowCount() {
        return photos.size();
    }

    public int getColumnCount() {
        return photoStocks.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        var key = getKey(photoStocks.get(columnIndex).ftpEndpoint(), photos.get(rowIndex));
        return statuses.get(key);
    }

    public PhotoStock getPhotoStock(int index) {
        return photoStocks.get(index);
    }

}
