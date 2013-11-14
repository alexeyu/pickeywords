package nl.alexeyu.photomate.ui;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.model.Photo;

public class PhotoTable<T extends Photo> extends JTable {
    
    private final int columnCount;
    
    private final PhotoObserver observer;
    
    public PhotoTable(int columnCount, PhotoObserver observer) {
        this.columnCount = columnCount;
        this.observer = observer;
        setModel(new StockPhotoTableModel(Collections.EMPTY_LIST));
        setDefaultRenderer(Object.class, new PhotoCellRenderer());
        getTableHeader().setPreferredSize(new Dimension(0,0));
        getTableHeader().setVisible(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
        ListSelectionListener selectionListener = new SelectionListener();
        getSelectionModel().addListSelectionListener(selectionListener);
        getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
        
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(this);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getViewport().setBackground(getBackground());
        
        if (observer instanceof JComponent) {
            ((JComponent) observer).add(sp);
        }
    }
    
    public void setPhotos(List<T> photos) {
        setModel(new StockPhotoTableModel(photos));
    }
    
    public T getSelectedPhoto() {
        return (T) getModel().getValueAt(getSelectedRow(), getSelectedColumn());
    }
    
    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            observer.photoSelected(getSelectedPhoto());
        }
        
    }
    
    private class StockPhotoTableModel extends AbstractTableModel {
        
        private final List<T> photos;
        
        public StockPhotoTableModel(List<T> photos) {
            this.photos = photos;
        }

        @Override
        public int getRowCount() {
            return Math.max(1, photos.size() / getColumnCount() + 
                    photos.size() % getColumnCount());
        }

        @Override
        public int getColumnCount() {
            return columnCount;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int index = rowIndex * getColumnCount() + columnIndex; 
            if (index < 0 || index >= photos.size()) {
                return null;
            }
            return photos.get(index);
        }
        
    }


}
