package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.Constants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.Constants.LINE_BORDER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionListener;

import nl.alexeyu.photomate.model.LocalPhoto;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoList implements PropertyChangeListener {
	
	private JList<LocalPhoto> photoList;
	private JScrollPane sp;
	
	public PhotoList(ListModel<LocalPhoto> listModel) {
		photoList = new JList<LocalPhoto>(listModel);
		photoList.setFixedCellHeight(Constants.THUMBNAIL_SIZE.height);
		photoList.setFixedCellWidth(Constants.THUMBNAIL_SIZE.width);
		photoList.setCellRenderer(new ThumbnailRenderer());
		
		sp = new JScrollPane(photoList);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportBorder(Constants.EMPTY_BORDER);
		sp.setBorder(Constants.EMPTY_BORDER);
	}

	public void addListener(ListSelectionListener listener) {
		photoList.addListSelectionListener(listener);
	}
	
	public JComponent getComponent() {
		return sp;
	}

	public void refresh() {
		photoList.clearSelection();
		photoList.revalidate();
		photoList.repaint();
	}


	@Override
    public void propertyChange(PropertyChangeEvent evt) {
	    if (evt.getPropertyName().equals("thumbnail")) {
	        photoList.repaint();
	    }
    }

    private static class ThumbnailRenderer implements ListCellRenderer<LocalPhoto> {

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
			int keywordCount = photo.getKeywords() == null ? 0 : photo.getKeywords().size(); 
			JLabel nameLabel = new JLabel(photo.getName() + "[" + keywordCount + "]");
			if (!photo.isReadyToUpload()) {
				nameLabel.setIcon(ImageUtils.getImage("error.png"));
			}
			panel.add(nameLabel, BorderLayout.NORTH);
			nameLabel.setForeground(Color.GRAY);
			panel.add(label, BorderLayout.CENTER);
			return panel;
		}

	}


}
