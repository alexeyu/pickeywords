package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.CLICKABLE_ICON_SIZE;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.api.PhotoFileProcessor;
import nl.alexeyu.photomate.api.archive.ArchivePhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public class PhotoTable<P extends AbstractPhoto> extends JTable implements PropertyChangeListener {
    
    private static final int CELL_HEIGHT = UiConstants.THUMBNAIL_SIZE.height + 16;
    
    private final List<P> emptyPhotos = Collections.emptyList();
    
    private final int columnCount;
    
    private final List<PhotoObserver<? super P>> observers = new ArrayList<>();
    
    public PhotoTable(int columnCount) {
    	this.columnCount = columnCount;
        init();
    }

    public PhotoTable(int columnCount, JComponent parent) {
    	this(columnCount);
    	initParent(parent);
    }
    
    private void init() {
        setPhotos(emptyPhotos);
        setDefaultRenderer(Object.class, new PhotoCellRenderer());
        
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
        ListSelectionListener selectionListener = new SelectionListener();
        getSelectionModel().addListSelectionListener(selectionListener);
        getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
        
        setRowHeight(CELL_HEIGHT);
        setPreferredScrollableViewportSize(UiConstants.THUMBNAIL_SIZE);
    }
    
    private void initParent(JComponent parent) {
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(this);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getViewport().setBackground(getBackground());

        getTableHeader().setPreferredSize(new Dimension(0,0));
        getTableHeader().setVisible(false);
        
        parent.add(sp);
    }

    public void addObserver(PhotoObserver<? super P> observer) {
        this.observers.add(observer);
    }

    public void setPhotos(List<P> photos) {
    	photos.forEach(photo -> photo.addPropertyChangeListener(this));
        final StockPhotoTableModel model = new StockPhotoTableModel(photos); 
        setModel(model);
        if (photos.size() > 0 && photos.get(0) instanceof ArchivePhoto) {
    		addMouseListener(new DeleteArchivedPhotoListener(model));
        }
    }
    
    private int getColumnRight(int col) {
    	return IntStream.range(0, col + 1)
    			.map(index -> getColumnModel().getColumn(index).getWidth())
    			.sum();
    }
    
    private int getRowTop(int row) {
    	return row  * getRowHeight(); 
    }

    public Optional<P> getSelectedPhoto() {
    	return getSelectedPhotoImpl();
    }
    
    @SuppressWarnings("unchecked")
    private Optional<P> getSelectedPhotoImpl() {    	
    	return ((StockPhotoTableModel) getModel()).getValueAt(getSelectedRow(), getSelectedColumn());
    }
    
    private final class DeleteArchivedPhotoListener extends MouseAdapter {
        private final PhotoTable<P>.StockPhotoTableModel model;
        
        private final PhotoFileProcessor cleaner = new PhotoFileCleaner();

        private DeleteArchivedPhotoListener(PhotoTable<P>.StockPhotoTableModel model) {
            this.model = model;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        	int row = rowAtPoint(e.getPoint());
        	int col = columnAtPoint(e.getPoint());
        	if (getColumnRight(col) - e.getPoint().x < CLICKABLE_ICON_SIZE && e.getPoint().y - getRowTop(row) < CLICKABLE_ICON_SIZE) {
        		Optional<P> photo = model.getValueAt(row, col);
        		if (photo.isPresent() && photo.get().thumbnail().isPresent()) {
        		    ArchivePhoto arcPhoto = (ArchivePhoto) photo.get();
        			arcPhoto.delete();
        			cleaner.process(arcPhoto.getPath());
        			repaint();
        		}
        	}
        }
    }

    private class SelectionListener implements ListSelectionListener {

        @Override
        @SuppressWarnings("rawtypes")
        public void valueChanged(ListSelectionEvent e) {
        	Optional photo = getSelectedPhotoImpl();
            observers.forEach((observer) -> observer.photoSelected(photo));
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
        public Optional<P> getValueAt(int rowIndex, int columnIndex) {
            int index = rowIndex * getColumnCount() + columnIndex; 
            if (index < 0 || index >= photos.size()) {
                return Optional.empty();
            }
            return Optional.of(photos.get(index));
        }
        
    }


}
