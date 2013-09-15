package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.Constants.EMPTY_BORDER;
import static nl.alexeyu.photomate.ui.Constants.LINE_BORDER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

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

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.UpdateListener;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoList implements UpdateListener<Photo> {
	
	private JList<Photo> photoList;
	private JScrollPane sp;
	
	public PhotoList(ListModel<Photo> listModel) {
		photoList = new JList<Photo>(listModel);
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

	public void onUpdate(Photo obj) {
		photoList.repaint();
	}

	private static class ThumbnailRenderer implements ListCellRenderer<Photo> {

		public Component getListCellRendererComponent(JList<? extends Photo> list, Photo photo,
				int index, boolean isSelected, boolean cellHasFocus) {
			Image thumbnail = photo.getThumbnail();
			JLabel label = new JLabel();
			if (thumbnail == null) {
				label.setText("Loading...");
			} else {
				label.setIcon(new ImageIcon(thumbnail));
			}
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(isSelected ? LINE_BORDER : EMPTY_BORDER);
			JLabel nameLabel = new JLabel(photo.getName() + " [" + photo.getKeywords().size() + "]");
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
