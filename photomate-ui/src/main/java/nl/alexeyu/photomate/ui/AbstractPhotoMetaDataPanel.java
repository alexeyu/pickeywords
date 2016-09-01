package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.PhotoObserver;

public abstract class AbstractPhotoMetaDataPanel<P extends AbstractPhoto> extends JPanel
        implements PropertyChangeListener, PhotoObserver<P> {

    protected HintedTextField captionEditor;

    protected HintedTextField descriptionEditor;

    protected JList<String> keywordList = new JList<>(new DefaultListModel<String>());

    protected Optional<P> photo = Optional.empty();

    public AbstractPhotoMetaDataPanel() {
        super(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        JPanel editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

        captionEditor = new HintedTextField("Caption", PhotoProperty.CAPTION.propertyName(), true);
        editorPanel.add(captionEditor);
        descriptionEditor = new HintedTextField("Description", PhotoProperty.DESCRIPTION.propertyName(), true);
        editorPanel.add(descriptionEditor);

        add(editorPanel, BorderLayout.NORTH);
        add(new JScrollPane(keywordList), BorderLayout.CENTER);

        setPreferredSize(UiConstants.PREVIEW_SIZE);
    }

    public final void setPhoto(Optional<P> photo) {
        this.photo.ifPresent(p -> p.removePropertyChangeListener(this));
        this.photo = photo;
        updateComponentsWithPhotoMetaData();
        this.photo.ifPresent(p -> p.addPropertyChangeListener(this));
    }

    private void updateComponentsWithPhotoMetaData() {
        boolean isNull = !photo.isPresent() || photo.get().metaData().isEmpty();
        captionEditor.setText(isNull ? "" : photo.get().metaData().caption());
        descriptionEditor.setText(isNull ? "" : photo.get().metaData().description());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (!isNull) {
            photo.get().keywords().forEach(listModel::addElement);
        }
        keywordList.setModel(listModel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (AbstractPhoto.METADATA_PROPERTY.equals(e.getPropertyName())) {
            updateComponentsWithPhotoMetaData();
        } else {
            firePropertyChange(e.getPropertyName(), null, e.getNewValue());
        }
    }

    @Override
    public void photoSelected(Optional<P> photo) {
        setPhoto(photo);
    }

}
