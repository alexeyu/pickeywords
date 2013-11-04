package nl.alexeyu.photomate.app;

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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.LocalPhotoManager;
import nl.alexeyu.photomate.ui.DirChooser;
import nl.alexeyu.photomate.ui.LocalPhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.PhotoList;
import nl.alexeyu.photomate.ui.PhotoStockPanel;
import nl.alexeyu.photomate.ui.UiConstants;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.ui.UploadTableModel;
import nl.alexeyu.photomate.util.ConfigReader;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main implements ListSelectionListener, PropertyChangeListener {

    private JFrame frame = new JFrame("Your Photo Mate");

    private PhotoList photoList = new PhotoList();

    private JButton uploadButton = new JButton();

    private LocalPhotoMetaDataPanel photoMetaDataPanel = new LocalPhotoMetaDataPanel();

    private JLabel photoPreview = new JLabel();

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
        
        photoList.getList().addListSelectionListener(this);
        dirChooser.addPropertyChangeListener("dir", this);
        dirChooser.init();

        frame.setSize(1200, 700);
        frame.setVisible(true);
    }

    private void buildGraphics() {
        final JPanel tagPane = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(photoMetaDataPanel, BorderLayout.CENTER);
        photoMetaDataPanel.addPropertyChangeListener(photoManager);

        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (photoManager.validatePhotos()) {
                    JComponent uploadPanel = createUploadPanel();
                    frame.getContentPane().remove(tagPane);
                    frame.getContentPane().add(uploadPanel);
                    photoManager.uploadPhotos();
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Cannot upload: there're unloaded photos or photos without tags.");
                }
            }
        });
        refreshUploadButton();

        photoPreview.setPreferredSize(Photo.PREVIEW_SIZE);
        centerPanel.add(photoPreview, BorderLayout.NORTH);

        centerPanel.setBorder(UiConstants.EMPTY_BORDER);
        tagPane.add(centerPanel, BorderLayout.WEST);
        photoStockPanel.build();
        tagPane.add(photoStockPanel, BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(UiConstants.EMPTY_BORDER);
        northPanel.add(dirChooser, BorderLayout.CENTER);
        northPanel.add(uploadButton, BorderLayout.EAST);

        frame.getContentPane().add(northPanel, BorderLayout.NORTH);
        frame.getContentPane().add(photoList.getComponent(), BorderLayout.WEST);
        frame.getContentPane().add(tagPane);
    }

    private JComponent createUploadPanel() {
        UploadTableModel tableModel = new UploadTableModel(photoManager.getPhotos(), configReader.getPhotoStocks());
        uploadTable.setModel(tableModel);
        return new JScrollPane(uploadTable);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        File dir = (File) e.getNewValue();
        if (dir != null) {
            photoManager.setPhotoFiles(dir.listFiles());
            photoList.setPhotos(photoManager.getPhotos());
            refreshUploadButton();
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

    public void valueChanged(ListSelectionEvent e) {
        JList<?> list = (JList<?>) e.getSource();
        LocalPhoto currentPhoto = (LocalPhoto) list.getSelectedValue();
        photoMetaDataPanel.setPhoto(currentPhoto);
        photoPreview.setIcon(currentPhoto == null ? null : currentPhoto.getPreview());
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new AppModule());
        injector.getInstance(Main.class).start();
    }

}
