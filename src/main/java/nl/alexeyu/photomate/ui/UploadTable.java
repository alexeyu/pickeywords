package nl.alexeyu.photomate.ui;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.UploadPhotoEvent;
import nl.alexeyu.photomate.service.UploadPhotoListener;
import nl.alexeyu.photomate.util.ImageUtils;

import org.apache.commons.lang3.StringUtils;

public class UploadTable extends JTable implements UploadPhotoListener {
	
	private static final int ROW_HEIGHT = 32;

	private static final int PHOTO_STOCK_ROW_HEIGHT = 40;

	private static final int PHOTO_COLUMN_WIDTH = 150;

	private final List<Photo> photos;
	
	private final List<PhotoStock> photoStocks;
	
	private final Map<String, UploadPhotoEvent> statuses;

	public UploadTable(List<Photo> photos, List<PhotoStock> photoStocks) {
		this.photos = photos;
		this.photoStocks = photoStocks;
		statuses = new HashMap<String, UploadPhotoEvent>();
		setModel(new UploadTableModel());
		setDefaultRenderer(Object.class, new UploadTableRenderer());

		getColumnModel().getColumn(0).setPreferredWidth(PHOTO_COLUMN_WIDTH);
		getColumnModel().getColumn(0).setWidth(PHOTO_COLUMN_WIDTH);
		setRowHeight(ROW_HEIGHT);
		setRowHeight(0, PHOTO_STOCK_ROW_HEIGHT);
	}

	private String getKey(PhotoStock photoStock, Photo photo) {
		return photoStock.getName() + "-" + photo.getName();
	}

	@Override
	public void statusChanged(UploadPhotoEvent event) {
		statuses.put(getKey(event.getPhotoStock(), event.getPhoto()), event);
		repaint();
	}

	private class UploadTableModel extends AbstractTableModel {

		public int getRowCount() {
			return photos.size() + 1;
		}

		public int getColumnCount() {
			return photoStocks.size() + 1;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == 0) {
				if (columnIndex == 0) {
					return "";
				}
				return photoStocks.get(columnIndex - 1);
			}
			if (columnIndex == 0) {
				return photos.get(rowIndex - 1);
			}
			String key = getKey(photoStocks.get(columnIndex - 1), photos.get(rowIndex - 1));
			return statuses.get(key);
		}
		
	}

	private class UploadTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = getComponentImpl(value);
			c.setForeground(Color.BLACK);
			return c;
		}
		
		private Component getComponentImpl(Object value) {
			if (value instanceof Photo) {
				Photo photo = (Photo) value;
				JLabel c = new JLabel(photo.getName());
				c.setToolTipText("<html><img src=\"file:/tmp/" + photo.getName() + "\"></html>");
				return c;
			}
			if (value instanceof PhotoStock) {
				PhotoStock photoStock = (PhotoStock) value;
				if (StringUtils.isNotBlank(photoStock.getIconUrl())) {
					URL url = getClass().getResource(photoStock.getIconUrl());
					return new JLabel(new ImageIcon(url));
				} else {
					return new JLabel(photoStock.getName());
				}
			}
			if (value instanceof UploadPhotoEvent) {
				return getStatusLabel((UploadPhotoEvent) value);
			}
			if (value == null) {
				return new JLabel(ImageUtils.getImage("queue.png"));
			}
			return new JLabel();
		}
		
		private JLabel getStatusLabel(UploadPhotoEvent event) {
			switch (event.getStatus()) {
			case QUEUED:
				return new JLabel(ImageUtils.getImage("queue.png"));
			case IN_PROGRESS:
				return new JLabel("   " + event.getPercentUploaded() + "%");
			case SUCCSESS:
				return new JLabel(ImageUtils.getImage("ok.png"));
			default:
				JLabel label = new JLabel(ImageUtils.getImage("error.png"));
				label.setToolTipText(event.getException().getMessage());
				return label;
			}
		}

	}
	
}
