package nl.alexeyu.photomate.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;

import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.upload.UploadPhotoListener;
import nl.alexeyu.photomate.util.ImageUtils;

@Singleton
public class UploadTable extends JTable implements UploadPhotoListener {
	
	private static final int ROW_HEIGHT = UiConstants.THUMBNAIL_SIZE.height;

	private static final int PHOTO_COLUMN_WIDTH = UiConstants.THUMBNAIL_SIZE.width + 4;

	private static final int PHOTO_STOCK_ROW_HEIGHT = 40;

	private UploadTableModel uploadModel;

	public UploadTable() {
		setDefaultRenderer(Object.class, new UploadTableRenderer());
	    setDefaultRenderer(Photo.class, new PhotoCellRenderer());
	    JTableHeader header = getTableHeader();
	    header.setDefaultRenderer(new HeaderRenderer());
	    header.setPreferredSize(new Dimension(1, PHOTO_STOCK_ROW_HEIGHT));
	    header.setBackground(Color.WHITE);
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		if (dataModel instanceof UploadTableModel) {
			this.uploadModel = (UploadTableModel) dataModel;
			getColumnModel().getColumn(0).setPreferredWidth(PHOTO_COLUMN_WIDTH);
			getColumnModel().getColumn(0).setMaxWidth(PHOTO_COLUMN_WIDTH);
			setRowHeight(ROW_HEIGHT);
		}
	}

	@Override
	public void onProgress(PhotoStock photoStock, EditablePhoto photo, long uploadedBytes) {
		Integer percent = (int) (uploadedBytes * 100 / photo.getFile().length());
		uploadModel.setStatus(photoStock, photo, percent);
		repaint();
	}

	@Override
	public void onSuccess(PhotoStock photoStock, EditablePhoto photo) {
		uploadModel.setStatus(photoStock, photo, "");
		repaint();
	}

	@Override
	public void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft) {
		uploadModel.setStatus(photoStock, photo, ex);
		repaint();
	}

    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setBorder(new LineBorder(Color.LIGHT_GRAY));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            if (column > 0) {
                PhotoStock photoStock = uploadModel.getPhotoStock(column - 1);
                if (photoStock.getIconUrl().isEmpty()) {
                    label.setText(photoStock.getName());
                } else {
                    URL url = getClass().getResource(photoStock.getIconUrl());
                    label.setIcon(new ImageIcon(url));
                }
            }
            return label;
        }
        
    }

	private class UploadTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
		    JLabel label = new JLabel();
		    label.setHorizontalAlignment(SwingConstants.CENTER);
			if (value instanceof Exception) {
				label.setIcon(ImageUtils.getImage("error.png"));
				Exception ex = (Exception) value;
				label.setToolTipText(ex.getMessage());
			} else  if (value instanceof Integer) {
				Integer progress = (Integer) value;
				label.setText(progress + "%");
			} else  if (value instanceof String) {
				label.setIcon(ImageUtils.getImage("ok.png"));
			} else {
			    label.setIcon(ImageUtils.getImage("queue.png"));
			}
			return label;
		}
		
	}
	
}
