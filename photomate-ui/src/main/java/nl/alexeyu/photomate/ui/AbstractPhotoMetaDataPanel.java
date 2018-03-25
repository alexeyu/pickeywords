package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.BORDER_WIDTH;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    protected P photo;

    public AbstractPhotoMetaDataPanel() {
        super(new BorderLayout(BORDER_WIDTH, BORDER_WIDTH));
        var editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

        captionEditor = new HintedTextField("Caption", PhotoProperty.CAPTION.propertyName(), true);
        editorPanel.add(captionEditor);
        descriptionEditor = new HintedTextField("Description", PhotoProperty.DESCRIPTION.propertyName(), true);
        editorPanel.add(descriptionEditor);

        add(editorPanel, BorderLayout.NORTH);
        add(new JScrollPane(keywordList), BorderLayout.CENTER);

        setPreferredSize(UiConstants.PREVIEW_SIZE);
    }

    public final void setPhoto(P photo) {
        if (this.photo != null) {
            this.photo.removePropertyChangeListener(this);
        }
        this.photo = photo;
        updateComponentsWithPhotoMetaData();
        if (this.photo != null) {
            this.photo.addPropertyChangeListener(this);
        }
    }

    private void updateComponentsWithPhotoMetaData() {
        var listModel = new DefaultListModel<String>();
        if (this.photo != null) {
            captionEditor.setText(photo.metaData().caption());
            descriptionEditor.setText(photo.metaData().description());
            photo.keywords().forEach(listModel::addElement);
        } else {
            captionEditor.setText("");
            descriptionEditor.setText("");
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
    public void photoSelected(P photo) {
        setPhoto(photo);
    }

}
