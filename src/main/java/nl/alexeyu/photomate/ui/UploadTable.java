package nl.alexeyu.photomate.ui;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;

import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.UploadPhotoListener;
import nl.alexeyu.photomate.util.ImageUtils;

@Singleton
public class UploadTable extends JTable implements UploadPhotoListener {
	
	private static final int ROW_HEIGHT = 125;

	private static final int PHOTO_STOCK_ROW_HEIGHT = 40;

	private static final int PHOTO_COLUMN_WIDTH = 120;

	private UploadTableModel uploadModel;

	public UploadTable() {
		setDefaultRenderer(Object.class, new UploadTableRenderer());
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		if (dataModel instanceof UploadTableModel) {
			this.uploadModel = (UploadTableModel) dataModel;
			getColumnModel().getColumn(0).setPreferredWidth(PHOTO_COLUMN_WIDTH);
			getColumnModel().getColumn(0).setWidth(PHOTO_COLUMN_WIDTH);
			setRowHeight(ROW_HEIGHT);
			setRowHeight(0, PHOTO_STOCK_ROW_HEIGHT);
		}
	}

	@Override
	public void onProgress(PhotoStock photoStock, Photo photo, long uploadedBytes) {
		Integer percent = (int) (uploadedBytes * 100 / photo.getFile().length());
		uploadModel.setStatus(photoStock, photo, percent);
		repaint();
	}

	@Override
	public void onSuccess(PhotoStock photoStock, Photo photo) {
		uploadModel.setStatus(photoStock, photo, "");
		repaint();
	}

	@Override
	public void onError(PhotoStock photoStock, Photo photo, Exception ex, int attemptsLeft) {
		uploadModel.setStatus(photoStock, photo, ex);
		repaint();
	}

	private class UploadTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (row == 0 && column == 0) {
				return new JLabel("Uploading " + (table.getRowCount() - 1) + " photos...");
			}
			return getComponentImpl(value);
		}
		
		private Component getComponentImpl(Object value) {
			if (value instanceof Photo) {
				Photo photo = (Photo) value;
				Image thumbnail = photo.getThumbnail();
				JLabel label = new JLabel();
				label.setIcon(new ImageIcon(thumbnail));
				return label;
			}
			if (value instanceof PhotoStock) {
				PhotoStock photoStock = (PhotoStock) value;
				if (photoStock.getIconUrl().isEmpty()) {
					return new JLabel(photoStock.getName());
				} else {
					URL url = getClass().getResource(photoStock.getIconUrl());
					return new JLabel(new ImageIcon(url));
				}
			}
			if (value instanceof Exception) {
				JLabel label = new JLabel(ImageUtils.getImage("error.png"));
				Exception ex = (Exception) value;
				label.setToolTipText(ex.getMessage());
				return label;
			}
			if (value instanceof Integer) {
				Integer progress = (Integer) value;
				return new JLabel("   " + progress + "%");
			}
			if (value instanceof String) {
				return new JLabel(ImageUtils.getImage("ok.png"));
			}
			return new JLabel(ImageUtils.getImage("queue.png"));
		}
		
	}
	
}
