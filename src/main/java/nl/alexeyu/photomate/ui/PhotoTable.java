package nl.alexeyu.photomate.ui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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

import nl.alexeyu.photomate.api.AbstractPhoto;

public class PhotoTable<P extends AbstractPhoto> extends JTable implements PropertyChangeListener {
    
    private static final int CELL_HEIGHT = UiConstants.THUMBNAIL_SIZE.height + 20;
    
    private final List<P> emptyPhotos = Collections.emptyList();
    
    private final int columnCount;
    
    private final List<PhotoObserver<? super P>> observers = new ArrayList<>();
    
    public PhotoTable(int columnCount, JComponent parent) {
        this.columnCount = columnCount;
        
        setPhotos(emptyPhotos);
        setDefaultRenderer(Object.class, new PhotoCellRenderer());
        
        getTableHeader().setPreferredSize(new Dimension(0,0));
        getTableHeader().setVisible(false);
        
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
        ListSelectionListener selectionListener = new SelectionListener();
        getSelectionModel().addListSelectionListener(selectionListener);
        getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
        
        setRowHeight(CELL_HEIGHT);
        setPreferredScrollableViewportSize(UiConstants.THUMBNAIL_SIZE);
        
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(this);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getViewport().setBackground(getBackground());
        
        parent.add(sp);
    }
    
    public void addObserver(PhotoObserver<? super P> observer) {
        this.observers.add(observer);
    }

    public void setPhotos(List<P> photos) {
        for (P photo : photos) {
            photo.addPropertyChangeListener(this);
        }
        setModel(new StockPhotoTableModel(photos));
    }
    
    @SuppressWarnings("unchecked")
    public P getSelectedPhoto() {
        return ((StockPhotoTableModel) getModel()).getValueAt(getSelectedRow(), getSelectedColumn());
    }
    
    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            P photo = getSelectedPhoto();
            for (PhotoObserver<? super P> observer : observers) {
                observer.photoSelected(photo);
            }
        }
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        repaint();
    }

    private class StockPhotoTableModel extends AbstractTableModel {
        
        private final List<P> photos;
        
        private final int rowCount;
        
        public StockPhotoTableModel(List<P> photos) {
            this.photos = photos;
            rowCount = Math.max(1, photos.size() / getColumnCount() + 
                    (photos.size() % getColumnCount() == 0 ? 0 : 1));
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
        public P getValueAt(int rowIndex, int columnIndex) {
            int index = rowIndex * getColumnCount() + columnIndex; 
            if (index < 0 || index >= photos.size()) {
                return null;
            }
            return photos.get(index);
        }
        
    }


}
