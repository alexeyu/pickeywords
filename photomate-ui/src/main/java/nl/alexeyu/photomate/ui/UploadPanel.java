package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.upload.UploadErrorEvent;
import nl.alexeyu.photomate.upload.UploadProgressEvent;
import nl.alexeyu.photomate.upload.UploadSuccessEvent;

public class UploadPanel extends JPanel {

    private final UploadTable uploadTable;

    public UploadPanel(List<EditablePhoto> photos, List<PhotoStock> photoStocks) {
        super(new BorderLayout());
        var model = new UploadTableModel(photos, photoStocks);

        var photoTableParent = new JPanel(new BorderLayout());
        PhotoTable<EditablePhoto> photoTable = new PhotoTable<>(1);
        photoTable.setPhotos(photos);
        photoTableParent.add(photoTable);
        photoTableParent.add(photoTable.getTableHeader(), BorderLayout.NORTH);

        var uploadTableParent = new JPanel(new BorderLayout());
        uploadTable = new UploadTable(model, photoTable);
        uploadTableParent.add(uploadTable);
        uploadTableParent.add(uploadTable.getTableHeader(), BorderLayout.NORTH);

        photoTable.setPreferredSize(UiConstants.THUMBNAIL_SIZE);
        photoTable.getTableHeader().setPreferredSize(uploadTable.getTableHeader().getPreferredSize());

        add(photoTableParent, BorderLayout.WEST);
        add(uploadTableParent, BorderLayout.CENTER);
    }

    @Subscribe
    public void onProgress(UploadProgressEvent ev) {
        var percent = (int) (ev.getBytesUploaded() * 100 / ev.getPhoto().fileSize());
        uploadTable.getModel().setStatus(ev.getEndpoint(), ev.getPhoto(), percent);
        uploadTable.repaint();
    }

    @Subscribe
    public void onSuccess(UploadSuccessEvent ev) {
        uploadTable.getModel().setStatus(ev.getEndpoint(), ev.getPhoto(), "");
        uploadTable.repaint();
    }

    @Subscribe
    public void onError(UploadErrorEvent ev) {
        uploadTable.getModel().setStatus(ev.getEndpoint(), ev.getPhoto(), ev.getException());
        uploadTable.repaint();
    }

}
