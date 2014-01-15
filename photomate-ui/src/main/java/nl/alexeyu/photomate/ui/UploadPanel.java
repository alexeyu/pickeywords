package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import nl.alexeyu.photomate.api.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;
import nl.alexeyu.photomate.service.upload.UploadPhotoListener;

public class UploadPanel extends JPanel implements UploadPhotoListener {
    
    private final UploadTable uploadTable;
    
    public UploadPanel(List<EditablePhoto> photos, List<PhotoStock> photoStocks) {
        super(new BorderLayout());
        UploadTableModel model = new UploadTableModel(photos, photoStocks);
        
        JPanel photoTableParent = new JPanel(new BorderLayout());
        PhotoTable<EditablePhoto> photoTable = new PhotoTable<>(1);
        photoTable.setPhotos(photos);
        photoTableParent.add(photoTable);
        photoTableParent.add(photoTable.getTableHeader(), BorderLayout.NORTH);

        JPanel uploadTableParent = new JPanel(new BorderLayout());
        uploadTable = new UploadTable(model, photoTable);
        uploadTableParent.add(uploadTable);
        uploadTableParent.add(uploadTable.getTableHeader(), BorderLayout.NORTH);
        
        photoTable.setPreferredSize(UiConstants.THUMBNAIL_SIZE);
        photoTable.getTableHeader().setPreferredSize(uploadTable.getTableHeader().getPreferredSize());
        
        add(photoTableParent, BorderLayout.WEST);
        add(uploadTableParent, BorderLayout.CENTER);
    }

    @Override
    public void onProgress(PhotoStock photoStock, EditablePhoto photo, long uploadedBytes) {
        Integer percent = (int) (uploadedBytes * 100 / photo.getFile().length());
        uploadTable.getModel().setStatus(photoStock, photo, percent);
        uploadTable.repaint();
    }

    @Override
    public void onSuccess(PhotoStock photoStock, EditablePhoto photo) {
        uploadTable.getModel().setStatus(photoStock, photo, "");
        uploadTable.repaint();
    }

    @Override
    public void onError(PhotoStock photoStock, EditablePhoto photo, Exception ex, int attemptsLeft) {
        uploadTable.getModel().setStatus(photoStock, photo, ex);
        uploadTable.repaint();
    }

}
