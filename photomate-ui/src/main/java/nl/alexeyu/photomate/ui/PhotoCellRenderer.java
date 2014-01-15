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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.api.ArchivePhoto;
import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoCellRenderer extends DefaultTableCellRenderer {
	
	private static final Color BACKGROUND = new JPanel().getBackground();
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
    	JComponent comp;
        if (value instanceof ArchivePhoto) {
        	comp = new ArchivePhotoLabel((ArchivePhoto) value, table.getColumnModel().getColumn(column).getWidth()).getComponent();
        } else if (value instanceof EditablePhoto) {
        	comp = new EdiatablePhotoPanel((EditablePhoto) value).getComponent(); 
        } else if (value instanceof Photo) {
        	comp = new PhotoLabel<Photo>((Photo) value).getComponent();
        } else if (value == null) {
        	comp = new JPanel();
        } else {
        	throw new IllegalArgumentException("Value must be instance of Photo");
        }
        comp.setBorder(isSelected ? LINE_BORDER : EMPTY_BORDER);
        comp.setBackground(BACKGROUND);
        return comp;
    }

    private static class PhotoLabel<T extends Photo> {
    	
    	protected final T photo;
    	
    	protected final JComponent component;
    	
    	public PhotoLabel(T photo) {
    		this.photo = photo;
    		component = createComponent();
    	}
    	
    	protected JComponent createComponent() {
			ImageIcon thumbnail = photo.getThumbnail();
			if (thumbnail != null) {
				return new JLabel(thumbnail);
			} else {
				return new JLabel("Loading...");
    		}
    	}
    	
    	public final JComponent getComponent() {
    		return component;
    	}

    }
    
    private static class ArchivePhotoLabel extends PhotoLabel<ArchivePhoto> {

    	private static final Image DELETE_IMAGE = ImageUtils.getImage("trash.png").getImage();

    	private final int columnWidth;

    	public ArchivePhotoLabel(ArchivePhoto photo, int columnWidth) {
    		super(photo);
    		this.columnWidth = columnWidth;
    	}

    	
		@Override
		protected JComponent createComponent() {
			ImageIcon thumbnail = photo.getThumbnail();
			if (thumbnail == null) {
				return super.createComponent();
			}
			return new JLabel(thumbnail) {
				@Override
				protected void paintComponent(Graphics g) {
					if (photo.isDeleted()) {
						 super.paintComponent(g);
						 Graphics2D g2d = (Graphics2D) g;
						 g2d.setStroke(new BasicStroke(5));
						 g2d.setPaint(Color.red);
						 g2d.drawLine(0, 0, columnWidth, UiConstants.THUMBNAIL_SIZE.height);
					} else {
						super.paintComponent(g);
						g.drawImage(DELETE_IMAGE, columnWidth - CLICKABLE_ICON_SIZE - BORDER_WIDTH * 2, 
								BORDER_WIDTH, CLICKABLE_ICON_SIZE, CLICKABLE_ICON_SIZE, null);
						
					}
				}

			};
		}
    }

    private static class EdiatablePhotoPanel extends PhotoLabel<EditablePhoto> {

		public EdiatablePhotoPanel(EditablePhoto photo) {
			super(photo);
		}

		@Override
		protected JComponent createComponent() {
	        JPanel panel = new JPanel(new BorderLayout());
			JComponent label = super.createComponent();
			panel.add(createTitle(photo), BorderLayout.NORTH);
	        panel.add(label, BorderLayout.CENTER);
	        return panel;
		}

	    private JComponent createTitle(EditablePhoto photo) {
	        String title = photo.getName();
	        PhotoMetaData metadata = photo.getMetaData();
	        if (metadata != null) {
	            title += " [" + metadata.getKeywords().size() + "]";
	        }
	        JLabel nameLabel = new JLabel(title);
	        if (!photo.isReadyToUpload()) {
	            nameLabel.setIcon(ImageUtils.getImage("error.png"));
	        }
	        nameLabel.setForeground(Color.GRAY);
	        return nameLabel;
	    }

    }
    
}
