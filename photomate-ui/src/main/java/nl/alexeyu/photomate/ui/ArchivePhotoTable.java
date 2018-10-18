package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.CLICKABLE_ICON_SIZE;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.JComponent;

import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.api.archive.ArchivePhoto;

public class ArchivePhotoTable extends PhotoTable<ArchivePhoto> {
	
	public ArchivePhotoTable(int columnCount, JComponent parent) {
		super(columnCount, parent);
	}

	public void setPhotos(List<ArchivePhoto> photos) {
		super.setPhotos(photos);
        if (photos.size() > 0) {
    		addMouseListener(new DeleteArchivedPhotoListener());
        }
	}
	
    private class DeleteArchivedPhotoListener extends MouseAdapter {

        private final Consumer<Path> cleaner = new PhotoFileCleaner();

		@Override
        public void mouseClicked(MouseEvent e) {
            int row = rowAtPoint(e.getPoint());
            int col = columnAtPoint(e.getPoint());
            if (getColumnRight(col) - e.getPoint().x < CLICKABLE_ICON_SIZE
                    && e.getPoint().y - getRowTop(row) < CLICKABLE_ICON_SIZE) {
                var photo = getModel().getValueAt(row, col);
                if (photo.isPresent()) {
                    var arcPhoto = photo.get();
                    arcPhoto.delete();
                    cleaner.accept(arcPhoto.getPath());
                    repaint();
                }
            }
        }
        
	    private int getRowTop(int row) {
	    	return row  * getRowHeight(); 
	    }
		
		private int getColumnRight(int col) {
        	return IntStream.range(0, col + 1)
        			.map(index -> getColumnModel().getColumn(index).getWidth())
        			.sum();
        }

    }


}
