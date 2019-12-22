package nl.alexeyu.photomate.app;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.swing.*;

import com.google.inject.Guice;
import com.google.inject.Injector;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.files.FileManager;
import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.EditablePhotoManager;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataReplicator;
import nl.alexeyu.photomate.ui.ArchivePhotoContainer;
import nl.alexeyu.photomate.ui.DirChooser;
import nl.alexeyu.photomate.ui.EditablePhotoContainer;
import nl.alexeyu.photomate.ui.EditablePhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.ReadonlyPhotoMetaDataPanel;
import nl.alexeyu.photomate.ui.StockPhotoContainer;
import nl.alexeyu.photomate.ui.UiConstants;
import nl.alexeyu.photomate.util.Configuration;

public class Main implements PropertyChangeListener {
	
    private final JFrame frame = new JFrame("Your Photo Mate");

    private static final String SHUTTERSTOCK_SOURCE = "Shutterstock";
    private static final String LOCAL_SOURCE = "Local";

    private final JButton uploadButton = new JButton("Upload");

    private EditablePhotoMetaDataPanel photoMetaDataPanel = new EditablePhotoMetaDataPanel();

    private ReadonlyPhotoMetaDataPanel sourcePhotoMetaDataPanel = new ReadonlyPhotoMetaDataPanel(
            photoMetaDataPanel.getDropTarget());

    private ExternalPhotoContainerRegistry photoSourceRegistry = new ExternalPhotoContainerRegistry();

    private DirChooser dirChooser;

    private PhotoMetadataReplicator replicator;

    @Inject
    private EditablePhotoManager photoManager;

    @Inject
    private EditablePhotoContainer editablePhotoContainer;

    @Inject
    private StockPhotoContainer stockPhotoContainer;

    @Inject
    private ArchivePhotoContainer archivePhotoContainer;

    @Inject
    private Configuration configuration;

    public void start() {
        registerPhotoSources();
        dirChooser = new DirChooser(configuration.getDefaultFolder());
        Function<Photo, Boolean> confirmator =  photo -> JOptionPane.showConfirmDialog(frame,
                "Do you want to replicate this photo's metadata to all selected photos?",
                photo.metaData().caption(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                photo.thumbnail()) == JOptionPane.YES_OPTION;
        replicator = new PhotoMetadataReplicator(editablePhotoContainer, confirmator, photoManager);
        initListeners();
        buildGraphics();
        dirChooser.init();
        activateWindow();
    }

    private void activateWindow() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1500, 900);
        frame.setVisible(true);
    }

    private void registerPhotoSources() {
        photoSourceRegistry.registerPhotoSource(LOCAL_SOURCE, archivePhotoContainer);
        photoSourceRegistry.registerPhotoSource(SHUTTERSTOCK_SOURCE, stockPhotoContainer);
    }

    private void initListeners() {
        editablePhotoContainer.addPhotoObserver(photoMetaDataPanel);
        editablePhotoContainer.addPhotoObserver(photoManager);
        editablePhotoContainer.setHighlightedPhotoConsumer(replicator);
        dirChooser.addPropertyChangeListener(DirChooser.DIR_PROPERTY, this);
        photoMetaDataPanel.addPropertyChangeListener(photoManager);
        sourcePhotoMetaDataPanel.addPropertyChangeListener(photoManager);
        stockPhotoContainer.addPhotoObserver(sourcePhotoMetaDataPanel);
        stockPhotoContainer.setHighlightedPhotoConsumer(replicator);
        archivePhotoContainer.addPhotoObserver(sourcePhotoMetaDataPanel);
        archivePhotoContainer.setHighlightedPhotoConsumer(replicator);
    }

    private void buildGraphics() {
        var centerPanel = new JPanel(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        centerPanel.setBorder(UiConstants.EMPTY_BORDER);
        centerPanel.add(prepareCurrentPhotoPanel(), BorderLayout.WEST);
        centerPanel.add(prepareSourcePhotosPanel(), BorderLayout.CENTER);

        frame.getContentPane().add(prepareLocalPhotosPanel(), BorderLayout.WEST);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
    }

    private JComponent prepareLocalPhotosPanel() {
        var p = new JPanel(new BorderLayout());
        p.add(dirChooser, BorderLayout.NORTH);
        p.add(editablePhotoContainer, BorderLayout.CENTER);
        return p;
    }

    private JComponent prepareCurrentPhotoPanel() {
        var p = new JPanel(new BorderLayout());
        p.setBorder(UiConstants.EMPTY_BORDER);
        p.add(editablePhotoContainer.getPreview(), BorderLayout.NORTH);
        p.add(photoMetaDataPanel, BorderLayout.CENTER);
        return p;
    }

    private JComponent prepareSourcePhotosPanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(UiConstants.EMPTY_BORDER);
        final CardLayout sourcesLayout = new CardLayout();
        final JPanel sourcesPanel = new JPanel(sourcesLayout);
        sourcesPanel.setPreferredSize(UiConstants.PREVIEW_SIZE);

        final ButtonGroup bgroup = new ButtonGroup();
        ActionListener changePhotoListener = new ChangePhotoListener(sourcesPanel, bgroup, sourcesLayout);

        var buttonsPanel = new JPanel();
        BoxLayout buttonsLayout = new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS);
        buttonsPanel.setLayout(buttonsLayout);
        buttonsPanel.setBorder(UiConstants.EMPTY_BORDER);

        buttonsPanel.add(new JLabel("Source:"));
        buttonsPanel.add(Box.createVerticalStrut(5));
        for (String sourceName : photoSourceRegistry.getSourceNames()) {
            sourcesPanel.add(photoSourceRegistry.getPhotoSource(sourceName), sourceName);
            sourcesPanel.add(archivePhotoContainer, LOCAL_SOURCE);
            JRadioButton rb = new JRadioButton(sourceName);
            rb.setActionCommand(sourceName);
            rb.addActionListener(changePhotoListener);
            rb.setSelected(bgroup.getButtonCount() == 0);
            bgroup.add(rb);
            buttonsPanel.add(rb);
        }
        changePhotoListener.actionPerformed(null);

        buttonsPanel.add(Box.createVerticalGlue());

        uploadButton.addActionListener(new UploadStarter());
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(uploadButton);

        var centerPanel = new JPanel(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        centerPanel.add(sourcePhotoMetaDataPanel, BorderLayout.WEST);
        centerPanel.add(buttonsPanel, BorderLayout.CENTER);

        panel.add(sourcesPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getNewValue() == null) {
            return;
        }
        var dir = Paths.get(e.getNewValue().toString());
        if (Files.exists(dir)) {
            List<EditablePhoto> photos = photoManager.createPhotos(dir);
            editablePhotoContainer.setPhotos(photos);
        }
    }

    private final class ChangePhotoListener implements ActionListener {
        private final JPanel sourcesPanel;
        private final ButtonGroup bgroup;
        private final CardLayout sourcesLayout;

        private ChangePhotoListener(JPanel sourcesPanel, ButtonGroup bgroup, CardLayout sourcesLayout) {
            this.sourcesPanel = sourcesPanel;
            this.bgroup = bgroup;
            this.sourcesLayout = sourcesLayout;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
            var sourceName = bgroup.getSelection().getActionCommand();
            sourcesLayout.show(sourcesPanel, sourceName);
            var photoContainer = photoSourceRegistry.getPhotoSource(sourceName);
            sourcePhotoMetaDataPanel.setPhoto(photoContainer.getActivePhoto().orElse(null));
        }
    }

    private class UploadStarter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<EditablePhoto> readyPhotos = photoManager.getPhotosReadyToUpload();
            Path uploadFolder = configuration.getUploadFolder();
            List<Path> paths = readyPhotos.stream().map(EditablePhoto::getPath).collect(Collectors.toList());
            FileManager.move(paths, uploadFolder);
        }
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new AppModule());
        injector.getInstance(Main.class).start();
    }

}
