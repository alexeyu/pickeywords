package nl.alexeyu.photomate.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

public class UploadTableModel extends AbstractTableModel {

	private final List<Photo> photos;

	private final List<PhotoStock> photoStocks;

	private final Map<String, Object> statuses;

	public UploadTableModel(List<Photo> photos, List<PhotoStock> photoStocks) {
		this.photos = photos;
		this.photoStocks = photoStocks;
		statuses = new HashMap<String, Object>();
	}

	public void setStatus(PhotoStock photoStock, Photo photo, Object status) {
		String key = getKey(photoStock, photo);
		statuses.put(key, status);
	}

	private String getKey(PhotoStock photoStock, Photo photo) {
		return photoStock.getName() + "-" + photo.getName();
	}

	public int getRowCount() {
		return photos.size() + 1;
	}

	public int getColumnCount() {
		return photoStocks.size() + 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == 0) {
			if (columnIndex == 0) {
				return null;
			}
			return photoStocks.get(columnIndex - 1);
		}
		if (columnIndex == 0) {
			return photos.get(rowIndex - 1);
		}
		String key = getKey(photoStocks.get(columnIndex - 1),
				photos.get(rowIndex - 1));
		return statuses.get(key);
	}

}
