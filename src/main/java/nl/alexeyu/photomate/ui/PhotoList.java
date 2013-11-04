package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.UiConstants.LINE_BORDER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoList implements PropertyChangeListener {
    
    private static final int CELL_HEIGHT = Photo.THUMBNAIL_SIZE.height + 20;
    private static final int CELL_WIDTH = Photo.THUMBNAIL_SIZE.width;
	
	private JList<LocalPhoto> photoList = new JList<>();
	private JScrollPane sp;
	
	public PhotoList() {
		photoList.setFixedCellHeight(CELL_HEIGHT);
		photoList.setFixedCellWidth(CELL_WIDTH);
		photoList.setCellRenderer(new ThumbnailRenderer());
		
		sp = new JScrollPane(photoList);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportBorder(UiConstants.EMPTY_BORDER);
		sp.setBorder(UiConstants.EMPTY_BORDER);
	}

	public void setPhotos(List<LocalPhoto> photos) {
	    ListModel<LocalPhoto> listModel = new PhotoListModel(photos);
	    photoList.setModel(listModel);
	    for (LocalPhoto photo : photos) {
	        photo.addPropertyChangeListener(this);
	    }
	}
	
	public JList<LocalPhoto> getList() {
		return photoList;
	}
	
	public JComponent getComponent() {
		return sp;
	}

	@Override
    public void propertyChange(PropertyChangeEvent e) {
	    photoList.repaint();
    }

    private static class ThumbnailRenderer implements ListCellRenderer<LocalPhoto> {

        @Override
		public Component getListCellRendererComponent(JList<? extends LocalPhoto> list, LocalPhoto photo,
				int index, boolean isSelected, boolean cellHasFocus) {
			ImageIcon thumbnail = photo.getThumbnail();
			JLabel label = new JLabel();
			if (thumbnail == null) {
				label.setText("Loading...");
			} else {
				label.setIcon(thumbnail);
			}
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(isSelected ? LINE_BORDER : EMPTY_BORDER);
			JLabel nameLabel = new JLabel(photo.getName());
			if (!photo.isReadyToUpload()) {
				nameLabel.setIcon(ImageUtils.getImage("error.png"));
			}
			panel.add(nameLabel, BorderLayout.NORTH);
			nameLabel.setForeground(Color.GRAY);
			panel.add(label, BorderLayout.CENTER);
			return panel;
		}

	}

    private static class PhotoListModel extends AbstractListModel<LocalPhoto> {
        
        private final List<LocalPhoto> photos;
        
        public PhotoListModel(List<LocalPhoto> photos) {
            this.photos = photos;
        }

        public int getSize() {
            return photos.size();
        }

        public LocalPhoto getElementAt(int index) {
            return photos.get(index);
        }
        
    }

}
