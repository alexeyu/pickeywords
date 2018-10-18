package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.PREVIEW_SIZE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public class EditablePhotoContainer extends PhotoContainer<EditablePhoto>
        implements PropertyChangeListener, PhotoObserver<EditablePhoto> {

    private static final int COLUMN_COUNT = 1;

    private JLabel photoPreview = new JLabel();

    public EditablePhotoContainer() {
        super(COLUMN_COUNT);
        photoTable.addObserver(this);
        photoPreview.setPreferredSize(PREVIEW_SIZE);
    }

    private void initPreview() {
        photoTable.getSelectedPhoto().ifPresentOrElse(
                photo -> photoPreview.setIcon(photo.preview()),
                () -> photoPreview.setIcon(null));
    }

    public void setPhotos(List<EditablePhoto> photos) {
        photos.forEach(photo -> photo.addPropertyChangeListener(this));
        photoTable.setPhotos(photos);
        if (photos.size() > 0) {
            photoTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    public JComponent getPreview() {
        return photoPreview;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        initPreview();
    }

    @Override
    public void photoSelected(EditablePhoto photo) {
        initPreview();
    }

}
