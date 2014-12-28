package nl.alexeyu.photomate.ui;

import java.util.List;
import java.util.Optional;

import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.api.AbstractPhoto;

class PhotoTableModel<P extends AbstractPhoto> extends AbstractTableModel {
    
    private final List<P> photos;
    
    private final int rowCount;
    
    private final int columnCount;
    
    public PhotoTableModel(List<P> photos, int tableCoumnCount) {
        this.photos = photos;
        this.columnCount = tableCoumnCount;
        int adjustedCount = photos.size() / columnCount + 
                (photos.size() % columnCount == 0 ? 0 : 1);
        rowCount = Math.max(1, adjustedCount);
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Optional<P> getValueAt(int rowIndex, int columnIndex) {
        int index = rowIndex * getColumnCount() + columnIndex; 
        if (index < 0 || index >= photos.size()) {
            return Optional.empty();
        }
        return Optional.of(photos.get(index));
    }
    
}