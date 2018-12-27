package nl.alexeyu.photomate.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.util.StaticImageProvider;

public class UploadTable extends JTable {

    private static final int PHOTO_STOCK_ROW_HEIGHT = 40;

    public UploadTable(UploadTableModel model, JTable sourceTable) {
        super(model);

        setRowHeight(sourceTable.getRowHeight());

        setDefaultRenderer(Object.class, new UploadTableRenderer());

        var header = getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(1, PHOTO_STOCK_ROW_HEIGHT));
        header.setBackground(Color.WHITE);
    }

    public UploadTableModel getModel() {
        return (UploadTableModel) super.getModel();
    }

    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            var label = new JLabel();
            label.setBorder(new LineBorder(Color.LIGHT_GRAY));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            var photoStock = getModel().getPhotoStock(column);
            if (photoStock.iconUrl().isEmpty()) {
                label.setText(photoStock.name());
            } else {
                var url = getClass().getResource(photoStock.iconUrl());
                label.setIcon(new ImageIcon(url));
            }
            return label;
        }

    }

    private class UploadTableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            var label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            if (value instanceof Exception) {
                label.setIcon(StaticImageProvider.getImage("error.png"));
                var ex = (Exception) value;
                label.setToolTipText(ex.getMessage());
            } else if (value instanceof Integer) {
                var progress = (Integer) value;
                label.setText(progress + "%");
            } else if (value instanceof String) {
                label.setIcon(StaticImageProvider.getImage("ok.png"));
            } else {
                label.setIcon(StaticImageProvider.getImage("queue.png"));
            }
            return label;
        }

    }

}
