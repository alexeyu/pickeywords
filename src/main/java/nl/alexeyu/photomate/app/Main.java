package nl.alexeyu.photomate.app;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.service.EditablePhotoManager;
import nl.alexeyu.photomate.ui.ArchivePhotoSource;
import nl.alexeyu.photomate.ui.DirChooser;
import nl.alexeyu.photomate.ui.EditablePhotoSource;
import nl.alexeyu.photomate.ui.ExternalPhotoSourceRegistry;
import nl.alexeyu.photomate.ui.LocalPhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.PhotoObserver;
import nl.alexeyu.photomate.ui.PhotoSource;
import nl.alexeyu.photomate.ui.ReadonlyPhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.StockPhotoSource;
import nl.alexeyu.photomate.ui.UiConstants;
import nl.alexeyu.photomate.ui.UploadTable;
import nl.alexeyu.photomate.ui.UploadTableModel;
import nl.alexeyu.photomate.util.ConfigReader;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main implements PropertyChangeListener, PhotoObserver<AbstractPhoto> {

    private JFrame frame = new JFrame("Your Photo Mate");
    
    private static final String SHUTTERSTOCK_SOURCE = "Shutterstock";
    private static final String LOCAL_SOURCE = "Local";

    private JButton uploadButton = new JButton();

    private LocalPhotoMetaDataPanel photoMetaDataPanel = new LocalPhotoMetaDataPanel();

    private ReadonlyPhotoMetaDataPanel sourcePhotoMetaDataPanel = new ReadonlyPhotoMetaDataPanel();
    
    private ExternalPhotoSourceRegistry photoSourceRegistry = new ExternalPhotoSourceRegistry();

    @Inject
    private EditablePhotoManager photoManager;

    @Inject
    private EditablePhotoSource editablePhotoSource;

    @Inject
    private StockPhotoSource stockPhotoSource;

    @Inject
    private ArchivePhotoSource archivePhotoSource;
    
    @Inject
    private UploadTable uploadTable;

    @Inject
    private DirChooser dirChooser;

    @Inject
    private ConfigReader configReader;

    public void start() {
        photoSourceRegistry.registerPhotoSource(LOCAL_SOURCE, archivePhotoSource);
        photoSourceRegistry.registerPhotoSource(SHUTTERSTOCK_SOURCE, stockPhotoSource);
        
        initListeners();
        buildGraphics();
        dirChooser.init();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setVisible(true);
    }
    
    private void initListeners() {
        editablePhotoSource.addPhotoObserver(photoMetaDataPanel);
        editablePhotoSource.addPhotoObserver(photoManager);
        dirChooser.addPropertyChangeListener("dir", this);
        photoMetaDataPanel.addPropertyChangeListener(photoManager);
        sourcePhotoMetaDataPanel.addPropertyChangeListener(photoManager);
        stockPhotoSource.addPhotoObserver(this);
        archivePhotoSource.addPhotoObserver(this);
    }

    private void buildGraphics() {
        JPanel centerPanel = new JPanel(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        centerPanel.setBorder(UiConstants.EMPTY_BORDER);
        centerPanel.add(prepareCurrentPhotoPanel(), BorderLayout.WEST);
        centerPanel.add(prepareSourcePhotosPanel(), BorderLayout.CENTER);

        frame.getContentPane().add(prepareLocalPhotosPanel(), BorderLayout.WEST);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
    }
    
    private JComponent prepareLocalPhotosPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(dirChooser, BorderLayout.NORTH);
        p.add(editablePhotoSource.getComponent(), BorderLayout.CENTER);
        return p;
    }
    
    private JComponent prepareCurrentPhotoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(UiConstants.EMPTY_BORDER);
        p.add(editablePhotoSource.getPreview(), BorderLayout.NORTH);
        p.add(photoMetaDataPanel, BorderLayout.CENTER);
        return p;
    }
    
    private JComponent prepareSourcePhotosPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(UiConstants.EMPTY_BORDER);
        final CardLayout sourcesLayout = new CardLayout();
        final JPanel sourcesPanel = new JPanel(sourcesLayout);
        sourcesPanel.setPreferredSize(UiConstants.PREVIEW_SIZE);
        JPanel centerPanel = new JPanel(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        centerPanel.add(sourcePhotoMetaDataPanel, BorderLayout.WEST);

        final ButtonGroup bgroup = new ButtonGroup();
        ActionListener l = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceName = bgroup.getSelection().getActionCommand();
                sourcesLayout.show(sourcesPanel, sourceName);
                PhotoSource<?> photoSource = photoSourceRegistry.getPhotoSource(sourceName);
                sourcePhotoMetaDataPanel.setPhoto(photoSource.getSelectedPhoto());
            }
        };

        JPanel buttonsPanel = new JPanel();
        BoxLayout buttonsLayout = new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS);
        buttonsPanel.setLayout(buttonsLayout);

        for (String sourceName : photoSourceRegistry.getSourceNames()) {
            sourcesPanel.add(photoSourceRegistry.getPhotoSource(sourceName).getComponent(), sourceName);
            sourcesPanel.add(archivePhotoSource.getComponent(), LOCAL_SOURCE);
            JRadioButton rb = new JRadioButton(sourceName);
            rb.setActionCommand(sourceName);
            rb.addActionListener(l);
            rb.setSelected(bgroup.getButtonCount() == 0);
            bgroup.add(rb);
            buttonsPanel.add(rb);
        }
        l.actionPerformed(null);
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Source"));
        prepareUploadButton();
        buttonsPanel.add(uploadButton, BorderLayout.SOUTH);
        centerPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        p.add(sourcesPanel, BorderLayout.NORTH);
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
        File dir = (File) e.getNewValue();
        if (dir != null && dir.exists()) {
            List<EditablePhoto> photos = photoManager.createPhotos(dir);
            editablePhotoSource.setPhotos(photos);
            refreshUploadButton();
        }
    }

    @Override
    public void photoSelected(AbstractPhoto photo) {
        sourcePhotoMetaDataPanel.setPhoto(photo);
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
