package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.model.PhotoMetaData.KEYWORDS_PROPERTY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.api.RemotePhoto;
import nl.alexeyu.photomate.api.shutterstock.ShutterPhotoStockApi;
import nl.alexeyu.photomate.model.Photo;

import com.google.inject.Inject;

public class PhotoStockPanel extends JPanel implements PropertyChangeListener {
    
    private HintedTextField keywordsToSearch;

    private AbstractPhotoMetaDataPanel photoMetaDataPanel = new LocalPhotoMetaDataPanel();

    private JTable photoTable;
    
    private JButton searchButton;
    
    @Inject
    private ShutterPhotoStockApi photoStockApi;
    
    private List<RemotePhoto> photos = new ArrayList<>();
    
    public PhotoStockPanel() {
        super(new BorderLayout(5, 5));
    }

    public void build() {
        keywordsToSearch = new HintedTextField("Search by:", "keyword_search");
        keywordsToSearch.addPropertyChangeListener(this);

        photoTable = new JTable(new StockPhotoTableModel());
        photoTable.setDefaultRenderer(Object.class, new PhotoCellRenderer());
        photoTable.setRowHeight(150);
        photoTable.getTableHeader().setVisible(false);
        photoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        photoTable.setCellSelectionEnabled(true);
        photoTable.setPreferredScrollableViewportSize(new Dimension(300, 150));

        photoTable.setBorder(new LineBorder(Color.red));
        
        photoTable.addMouseMotionListener(new PhotoPointer());
        photoTable.addMouseListener(new PhotoSelector());
        
        add(keywordsToSearch, BorderLayout.NORTH);
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
        centerPane.add(new JScrollPane(photoTable));
        centerPane.add(photoMetaDataPanel);
        add(centerPane);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("thumbnail")) {
            photoTable.repaint();
        } else if (e.getPropertyName().equals("keyword_search")) {
            photos = photoStockApi.search(e.getNewValue().toString());
            for (RemotePhoto photo : photos) {
                photo.addPropertyChangeListener(PhotoStockPanel.this);
            }
            photoTable.revalidate();
            photoTable.repaint();
        }
    }

    public void setListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    private class StockPhotoTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return Math.max(1, photos.size() / getColumnCount() + 
                    photos.size() % getColumnCount());
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int index = rowIndex * getColumnCount() + columnIndex; 
            if (index >= photos.size()) {
                return null;
            }
            return photos.get(index);
        }
        
    }

    private class PhotoPointer extends MouseMotionAdapter {
        
        private int row = -1;
        
        private int col = -1;

        @Override
        public void mouseMoved(MouseEvent e) {
            int columnIndex = photoTable.columnAtPoint(e.getPoint());
            int rowIndex = photoTable.rowAtPoint(e.getPoint());
            if (row != rowIndex || col != columnIndex) {
                col = columnIndex;
                row = rowIndex;
                AbstractPhoto photo = (AbstractPhoto) photoTable.getModel().getValueAt(rowIndex, columnIndex);
                photoMetaDataPanel.setPhoto(photo);
                photoTable.changeSelection(rowIndex, columnIndex, true, false);
                photoTable.repaint();
            }
        }
        
    }
    
    private class PhotoSelector extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                int columnIndex = photoTable.getSelectedColumn();
                int rowIndex = photoTable.getSelectedRow();
                Photo photo = (Photo) photoTable.getModel().getValueAt(rowIndex, columnIndex);
                firePropertyChange(KEYWORDS_PROPERTY, null, photo.getMetaData().getKeywords());
            }
        }
        
    }
        
}
