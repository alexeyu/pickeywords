package nl.alexeyu.photomate.app;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.service.LocalPhotoManager;
import nl.alexeyu.photomate.ui.DirChooser;
import nl.alexeyu.photomate.ui.LocalPhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.PhotoStockPanel;
import nl.alexeyu.photomate.ui.PhotoView;
import nl.alexeyu.photomate.ui.ReadonlyPhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.UiConstants;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.ui.UploadTableModel;
import nl.alexeyu.photomate.util.ConfigReader;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main implements PropertyChangeListener {

    private JFrame frame = new JFrame("Your Photo Mate");

    private PhotoView localPhotoList = new PhotoView();

    private JButton uploadButton = new JButton();

    private LocalPhotoMetaDataPanel photoMetaDataPanel = new LocalPhotoMetaDataPanel();

    private ReadonlyPhotoMetaDataPanel sourcePhotoMetaDataPanel = new ReadonlyPhotoMetaDataPanel();

    @Inject
    private LocalPhotoManager photoManager;

    @Inject
    private PhotoStockPanel photoStockPanel;

    @Inject
    private UploadTable uploadTable;

    @Inject
    private DirChooser dirChooser;

    @Inject
    private ConfigReader configReader;

    public void start() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        buildGraphics();
        
        localPhotoList.addPropertyChangeListener(this);
        dirChooser.addPropertyChangeListener("dir", this);
        dirChooser.init();

        frame.setSize(1200, 700);
        frame.setVisible(true);
    }

    private void buildGraphics() {
        photoMetaDataPanel.addPropertyChangeListener(photoManager);
        sourcePhotoMetaDataPanel.addPropertyChangeListener(photoManager);
        photoStockPanel.addPropertyChangeListener(this);

        JPanel centerPanel = new JPanel(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        centerPanel.add(prepareCurrentPhotoPanel(), BorderLayout.WEST);
        centerPanel.add(prepareSourcePhotosPanel(), BorderLayout.CENTER);
        centerPanel.setBorder(UiConstants.EMPTY_BORDER);

        frame.getContentPane().add(prepareLocalPhotosPanel(), BorderLayout.WEST);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
    }
    
    private JComponent prepareLocalPhotosPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(dirChooser, BorderLayout.NORTH);
        p.add(localPhotoList, BorderLayout.CENTER);
        prepareUploadButton();
        p.add(uploadButton, BorderLayout.SOUTH);
        return p;
    }
    
    private JComponent prepareCurrentPhotoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(UiConstants.EMPTY_BORDER);
        p.add(localPhotoList.getPreview(), BorderLayout.NORTH);
        p.add(photoMetaDataPanel, BorderLayout.CENTER);
        return p;
    }
    
    private JComponent prepareSourcePhotosPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(UiConstants.EMPTY_BORDER);
        photoStockPanel.setPreferredSize(AbstractPhoto.PREVIEW_SIZE);
        p.add(photoStockPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(sourcePhotoMetaDataPanel, BorderLayout.WEST);
        p.add(centerPanel, BorderLayout.CENTER);
        return p;
    }
    
    private void prepareUploadButton() {
        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (photoManager.validatePhotos()) {
                    UploadTableModel tableModel = new UploadTableModel(photoManager.getPhotos(), configReader.getPhotoStocks());
                    uploadTable.setModel(tableModel);
                    JComponent uploadPanel = new JScrollPane(uploadTable);
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(uploadPanel);
                    frame.revalidate();
                    frame.repaint();
                    photoManager.uploadPhotos();
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Cannot upload: there're unloaded photos or photos without tags.");
                }
            }
        });
        refreshUploadButton();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
        case "dir":
            File dir = (File) e.getNewValue();
            if (dir != null && dir.exists()) {
                photoManager.setPhotoFiles(dir.listFiles());
                localPhotoList.setPhotos(photoManager.getPhotos());
                refreshUploadButton();
            }
            break;
        case "photo":
            if (e.getSource() == localPhotoList) {
                LocalPhoto currentPhoto = (LocalPhoto) e.getNewValue();
                photoMetaDataPanel.setPhoto(currentPhoto);
                photoManager.setCurrentPhoto(currentPhoto);
            } else if (e.getSource() == photoStockPanel) {
                sourcePhotoMetaDataPanel.setPhoto((AbstractPhoto) e.getNewValue());
            }
        }
    }

    private void refreshUploadButton() {
        if (photoManager.getPhotos().size() > 0) {
            uploadButton.setText(">> [" + photoManager.getPhotos().size() + "]");
            uploadButton.setEnabled(true);
        } else {
            uploadButton.setText("The photos are not ready yet");
            uploadButton.setEnabled(false);
        }
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new AppModule());
        injector.getInstance(Main.class).start();
    }

}
