package nl.alexeyu.photomate.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.util.ImageUtils;

public class UploadTable extends JTable {
	
	private static final int PHOTO_STOCK_ROW_HEIGHT = 40;

	public UploadTable(UploadTableModel model, JTable sourceTable) {
        super(model);
        
        setRowHeight(sourceTable.getRowHeight());

        setDefaultRenderer(Object.class, new UploadTableRenderer());
	    
	    JTableHeader header = getTableHeader();
	    header.setDefaultRenderer(new HeaderRenderer());
	    header.setPreferredSize(new Dimension(1, PHOTO_STOCK_ROW_HEIGHT));
	    header.setBackground(Color.WHITE);
	}

	public UploadTableModel getModel() {
	    return (UploadTableModel) super.getModel();
	}

    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setBorder(new LineBorder(Color.LIGHT_GRAY));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            PhotoStock photoStock = getModel().getPhotoStock(column);
            if (photoStock.iconUrl().isEmpty()) {
                label.setText(photoStock.name());
            } else {
                URL url = getClass().getResource(photoStock.iconUrl());
                label.setIcon(new ImageIcon(url));
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
