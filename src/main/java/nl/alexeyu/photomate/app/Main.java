package nl.alexeyu.photomate.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
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

import nl.alexeyu.photomate.api.ShutterPhotoStockApi;
import nl.alexeyu.photomate.model.LocalPhoto;
import nl.alexeyu.photomate.model.StockPhotoDescription;
import nl.alexeyu.photomate.service.PhotoUploader;
import nl.alexeyu.photomate.service.UpdateListener;
import nl.alexeyu.photomate.service.keyword.ExifKeywordReader;
import nl.alexeyu.photomate.service.thumbnail.ImgscalrThumbnailingTask;
import nl.alexeyu.photomate.ui.Constants;
import nl.alexeyu.photomate.ui.KeywordPicker;
import nl.alexeyu.photomate.ui.PhotoChooser;
import nl.alexeyu.photomate.ui.PhotoList;
import nl.alexeyu.photomate.ui.PhotoStockPanel;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.ui.UploadTableModel;
import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.ImageUtils;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main implements UpdateListener<File>, ListSelectionListener {
	
	private List<LocalPhoto> photos = new ArrayList<>();
	
	private JFrame frame;
	
	private PhotoList photoList;
	
	private KeywordPicker keywordsPicker = new KeywordPicker();
	
	private LocalPhoto currentPhoto = LocalPhoto.NULL_PHOTO;

	private JButton uploadButton = new JButton();
	
	@Inject
	private PhotoStockPanel photoStockPanel;

	@Inject
	private ExifKeywordReader keywordReader;

	@Inject
	private UploadTable uploadTable;
	
	@Inject
	private ConfigReader configReader;
	
	@Inject
	private ExecutorService executor;
	
	@Inject
	private PhotoUploader photoUploader;

	public Main() {
		frame = new JFrame("Your Photo Mate");
		frame.getContentPane().setLayout(new CardLayout());
		photoList = new PhotoList(new PhotoListModel());
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
		tagPane.add(new PhotoChooser(frame, this, configReader), BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		centerPanel.add(keywordsPicker.getComponent(), BorderLayout.WEST);
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
		uploadButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (validatePhotos()) {
					JPanel uploadPane = new JPanel(new BorderLayout());
					UploadTableModel tableModel = new UploadTableModel(photos, configReader.getPhotoStocks());
					uploadTable.setModel(tableModel);
					uploadPane.add(new JScrollPane(uploadTable));
					frame.getContentPane().add(uploadPane, "UPLOAD");
					CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
					cardLayout.next(frame.getContentPane());
					photoUploader.uploadPhotos(photos);
				} else {
					JOptionPane.showMessageDialog(frame, "Cannot upload: there're unloaded photos or photos without tags.");
				}
			}
		});
		refreshUploadButton();
		buttonPanel.add(uploadButton);
		centerPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		photoStockPanel.build();
		centerPanel.add(photoStockPanel, BorderLayout.CENTER);
		
		centerPanel.setBorder(Constants.EMPTY_BORDER);
		tagPane.add(centerPanel);
		
		frame.getContentPane().add(tagPane, "TAG");
	}
	
	private boolean validatePhotos() {
		if (photos.size() == 0) {
			return false;
		}
		for (LocalPhoto photo : photos) {
			if (!photo.isReadyToUpload()) {
				return false;
			}
		}
		return true;
	}
	
	public void onUpdate(File dir) {
		photos.clear();
		for (File file : dir.listFiles()) {
			if (ImageUtils.isJpeg(file)) {
				LocalPhoto photo = new LocalPhoto(file);
				photos.add(photo);
				keywordReader.readKeywords(photo);
				scheduleThumbnail(photo);
			}
		}
		photoList.refresh();
		refreshUploadButton();
	}
	
	private void refreshUploadButton() {
		if (photos.size() > 0) {
			uploadButton.setText("Upload " + photos.size() + " photos");
			uploadButton.setEnabled(true);
		} else {
			uploadButton.setText("The photos are not ready yet");
			uploadButton.setEnabled(false);
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		JList<?> list = (JList<?>) e.getSource();
		currentPhoto = list.getSelectedValue() == null 
				? LocalPhoto.NULL_PHOTO 
				: (LocalPhoto) list.getSelectedValue();
		keywordsPicker.setPhoto(currentPhoto);
	}

	@Inject
	public void setKeywordReader(ExifKeywordReader keywordReader) {
		this.keywordReader = keywordReader;
		this.keywordReader.setListener(photoList);
	}

	private void scheduleThumbnail(LocalPhoto photo) {
		executor.execute(new ImgscalrThumbnailingTask(photo, photoList));
	}

	private class PhotoListModel extends AbstractListModel<LocalPhoto> {

		public int getSize() {
			return photos.size();
		}

		public LocalPhoto getElementAt(int index) {
			return photos.get(index);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		 Injector injector = Guice.createInjector(new AppModule());
		 injector.getInstance(Main.class).start();
	}

}
