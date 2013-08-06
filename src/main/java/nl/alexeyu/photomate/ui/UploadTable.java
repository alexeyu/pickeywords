package nl.alexeyu.photomate.ui;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.UploadPhotoListener;
import nl.alexeyu.photomate.util.ImageUtils;

import org.apache.commons.lang3.StringUtils;

public class UploadTable extends JTable implements UploadPhotoListener {
	
	private static final int ROW_HEIGHT = 32;

	private static final int PHOTO_STOCK_ROW_HEIGHT = 40;

	private static final int PHOTO_COLUMN_WIDTH = 150;

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
		uploadModel.setStatus(photoStock, photo, StringUtils.EMPTY);
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
				return null;
			}
			return getComponentImpl(value);
		}
		
		private Component getComponentImpl(Object value) {
			if (value instanceof Photo) {
				Photo photo = (Photo) value;
				JLabel c = new JLabel(photo.getName());
				String filePath = ImageUtils.getThumbnailFile(photo).getAbsolutePath();
				String html = String.format("<html><img src=\"file:%s\"></html>", filePath);
				c.setToolTipText(html);
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
