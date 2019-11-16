package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;
import static nl.alexeyu.photomate.ui.UiConstants.CLICKABLE_ICON_SIZE;
import static nl.alexeyu.photomate.ui.UiConstants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.UiConstants.LINE_BORDER;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Optional;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.api.archive.ArchivePhoto;
import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.util.StaticImageProvider;

public class PhotoCellRenderer extends DefaultTableCellRenderer {
	
    private static final Color BACKGROUND = new JPanel().getBackground();

    private final Function<Integer, Boolean> rowSelected;

    public PhotoCellRenderer() {
        this(x -> false);
    }

    public PhotoCellRenderer(Function<Integer, Boolean> rowSelected) {
        this.rowSelected = rowSelected;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
    	var comp = createComponent(table, value, column, rowSelected.apply(row));
        comp.setBorder(isSelected ? LINE_BORDER : EMPTY_BORDER);
        comp.setBackground(BACKGROUND);
        return comp;
    }

    private JComponent createComponent(JTable table, Object value, int column, boolean selected) {
        if (value instanceof Optional) {
            return createComponent(table, ((Optional<?>) value).orElse(null), column, selected);
        }
        if (value instanceof ArchivePhoto) {
            int columnWidth = table.getColumnModel().getColumn(column).getWidth();
            return new ArchivePhotoLabel((ArchivePhoto) value, columnWidth).getComponent();
        } else if (value instanceof EditablePhoto) {
            return new EdiatablePhotoLabel((EditablePhoto) value, selected).getComponent();
        } else if (value instanceof Photo) {
            return new PhotoLabel<>((Photo) value).getComponent();
        } else if (value == null) {
            return new JPanel();
        } else {
            throw new IllegalArgumentException("Value must be instance of Photo");
        }
    }

    private static class PhotoLabel<T extends Photo> {

        final T photo;

        JComponent component;

        public PhotoLabel(T photo) {
            this.photo = photo;
        }

        protected JComponent createComponent() {
            var thumbnail = photo.thumbnail();
            return thumbnail.getImage() != null ? new JLabel(thumbnail) : new JLabel("Loading...");
        }

        public final JComponent getComponent() {
            if (component == null) {
                component = createComponent();
            }
            return component;
        }

    }
    
    private static class ArchivePhotoLabel extends PhotoLabel<ArchivePhoto> {

    	private static final Image DELETE_IMAGE = StaticImageProvider.getImage("trash.png").getImage();

    	private final int columnWidth;

        public ArchivePhotoLabel(ArchivePhoto photo, int columnWidth) {
            super(photo);
            this.columnWidth = columnWidth;
        }
    	
        @Override
        protected JComponent createComponent() {
            var thumbnail = photo.thumbnail();
            if (thumbnail.getImage() == null) {
                return super.createComponent();
            }
            return new JLabel(thumbnail) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (photo.isDeleted()) {
                        var g2d = (Graphics2D) g;
                        g2d.setStroke(new BasicStroke(5));
                        g2d.setPaint(Color.red);
                        g2d.drawLine(0, 0, columnWidth, UiConstants.THUMBNAIL_SIZE.height);
                    } else {
                        g.drawImage(DELETE_IMAGE, columnWidth - CLICKABLE_ICON_SIZE - BORDER_WIDTH * 2, BORDER_WIDTH,
                                CLICKABLE_ICON_SIZE, CLICKABLE_ICON_SIZE, null);

                    }
                }

            };
        }
    }

    private static class EdiatablePhotoLabel extends PhotoLabel<EditablePhoto> {

        private final boolean selected;

        public EdiatablePhotoLabel(EditablePhoto photo, boolean selected) {
            super(photo);
            this.selected = selected;
        }

        @Override
        protected JComponent createComponent() {
            var panel = new JPanel(new BorderLayout());
            var label = super.createComponent();
            panel.add(createTitle(photo), BorderLayout.NORTH);
            panel.add(label, BorderLayout.CENTER);
            return panel;
        }

        private JComponent createTitle(EditablePhoto photo) {
            var panel = new JPanel(new BorderLayout());
            var title = photo.name();
            var metadata = photo.metaData();
            if (!metadata.isEmpty()) {
                title += " [" + metadata.keywords().size() + "]";
            }
            var nameLabel = new JLabel(title);
            if (!photo.isReadyToUpload()) {
                nameLabel.setIcon(StaticImageProvider.getImage("error.png"));
            }
            nameLabel.setForeground(Color.GRAY);
            panel.add(nameLabel, BorderLayout.WEST);
            var selector = new JCheckBox("");
            panel.add(selector, BorderLayout.EAST);
            selector.setSelected(selected);
            return panel;
        }

    }
    
}
