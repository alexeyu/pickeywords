package nl.alexeyu.photomate.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.ExecutorServices;
import nl.alexeyu.photomate.service.FtpUploadTask;
import nl.alexeyu.photomate.service.KeywordReader;
import nl.alexeyu.photomate.service.ThumbnailingTask;
import nl.alexeyu.photomate.service.UpdateListener;
import nl.alexeyu.photomate.service.exif.ExifKeywordReader;
import nl.alexeyu.photomate.ui.Constants;
import nl.alexeyu.photomate.ui.KeywordPicker;
import nl.alexeyu.photomate.ui.PhotoChooser;
import nl.alexeyu.photomate.ui.PhotoList;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.util.ConfigReader;


public class Main implements UpdateListener<File>, ListSelectionListener {
	
	private List<Photo> photos = new ArrayList<>();
	
	private KeywordReader keywordReader = new ExifKeywordReader();
	
	private JFrame frame;
	
	private PhotoList photoList;
	
	private KeywordPicker keywordsPicker;
	
	private Photo currentPhoto = Photo.NULL_PHOTO;

	private JButton uploadButton;
	
	private List<PhotoStock> photoStocks;

	private UploadTable uploadTable;
	
	public Main() {
		frame = new JFrame("Image Keywords");
		frame.getContentPane().setLayout(new CardLayout());
		photoList = new PhotoList(new PhotoListModel());
		keywordsPicker = new KeywordPicker();
		photoStocks = new ConfigReader().getPhotoStocks();
	}
	
	public void start() {
		frame.setSize(1200, 700);
		buildGraphics();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void buildGraphics() {
		JPanel tagPane = new JPanel(new BorderLayout());
		tagPane.add(photoList.getComponent(), BorderLayout.WEST);
		photoList.addListener(this);
		tagPane.add(new PhotoChooser(frame, this),  BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(keywordsPicker.getComponent());
		keywordsPicker.setAddKeywordListener(new UpdateListener<String>() {

			public void onUpdate(String keyword) {
				keywordReader.addKeyword(currentPhoto, keyword);
				keywordsPicker.setPhoto(currentPhoto);
			}
			
		});
		
		keywordsPicker.setRemoveKeywordListener(new UpdateListener<String>() {

			public void onUpdate(String keyword) {
				keywordReader.removeKeyword(currentPhoto, keyword);
				keywordsPicker.setPhoto(currentPhoto);
			}
			
		});
		
		JPanel buttonPanel = new JPanel();
		uploadButton = new JButton("Upload >>");
		uploadButton.setEnabled(false);
		uploadButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (photos.size() > 0 && validatePhotos()) {
					JPanel uploadPane = new JPanel(new BorderLayout());
					uploadTable = new UploadTable(photos, photoStocks);
					uploadPane.add(new JScrollPane(uploadTable));
					frame.getContentPane().add(uploadPane, "UPLOAD");
					CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
					cardLayout.next(frame.getContentPane());
					uploadPhotos();
				} else {
					JOptionPane.showMessageDialog(frame, "Cannot upload: there're photos without tags.");
				}
			}
		});
		buttonPanel.add(uploadButton);
		centerPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		centerPanel.setBorder(Constants.EMPTY_BORDER);
		tagPane.add(centerPanel);
		
		frame.getContentPane().add(tagPane, "TAG");
	}
	
	private void uploadPhotos() {
		for (Photo photo : photos) {
			for (PhotoStock photoStock : photoStocks) {
				FtpUploadTask uploadTask = new FtpUploadTask(photoStock, photo, uploadTable);
				ExecutorServices.getHeavyTasksExecutor().execute(uploadTask);
			}
		}
	}
	
	private boolean validatePhotos() {
		for (Photo photo : photos) {
			if (photo.getKeywords().size() == 0) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean isJpeg(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(".jpg");
	}

	public void onUpdate(File dir) {
		photos.clear();
		for (File file : dir.listFiles()) {
			if (isJpeg(file)) {
				Photo photo = new Photo(file);
				photos.add(photo);
				keywordReader.readKeywords(photo);
				scheduleThumbnail(photo);
			}
		}
		photoList.refresh();
		uploadButton.setEnabled(photos.size() > 0);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		JList<?> list = (JList<?>) e.getSource();
		currentPhoto = list.getSelectedValue() == null 
				? Photo.NULL_PHOTO 
				: (Photo) list.getSelectedValue();
		if (currentPhoto != null) {
			keywordsPicker.setPhoto(currentPhoto);
		}
	}

	private void scheduleThumbnail(Photo photo) {
		ExecutorServices.getHeavyTasksExecutor().execute(
				new ThumbnailingTask(photo, photoList));
	}

	private class PhotoListModel extends AbstractListModel<Photo> {

		public int getSize() {
			return photos.size();
		}

		public Photo getElementAt(int index) {
			return photos.get(index);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		new Main().start();
	}

}
