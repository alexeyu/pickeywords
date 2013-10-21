package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nl.alexeyu.photomate.api.ShutterPhotoStockApi;
import nl.alexeyu.photomate.model.Photo;

import com.google.inject.Inject;

public class PhotoStockPanel extends JPanel implements PropertyChangeListener {
    
    private JTextField keywordToSearch;

    private JList<String> keywordsList;

    private JTable photoTable;
    
    private JButton searchButton;
    
    @Inject
    private ShutterPhotoStockApi photoStockApi;
    
    private List<Photo> photos = new ArrayList<>();
    
    public PhotoStockPanel() {
        super(new BorderLayout(5, 5));
    }

    public void build() {
        JLabel label = new JLabel("Keyword to lookup:");
        keywordToSearch = new JTextField();
        searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchListener());
        
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        northPanel.add(label);
        northPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        northPanel.add(keywordToSearch);
        northPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        northPanel.add(searchButton);

        photoTable = new JTable(new StockPhotoTableModel());
        photoTable.setDefaultRenderer(Object.class, new PhotoCellRenderer());
        photoTable.setRowHeight(150);
        photoTable.getTableHeader().setVisible(false);
        photoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        photoTable.setCellSelectionEnabled(true);
        
        photoTable.addMouseMotionListener(new PhotoPointer());
        
        keywordsList = new JList<>();
        keywordsList.setPreferredSize(new Dimension(250, 0));
        
        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(keywordsList), BorderLayout.WEST);
        add(new JScrollPane(photoTable), BorderLayout.CENTER);
        
        photoStockApi.addPropertyChangeListener(this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("thumbnail")) {
            photoTable.repaint();
        }
    }

    public void setListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public String getText() {
        return keywordToSearch.getText().trim();
    }
    
    private class SearchListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (keywordToSearch.getText().length() > 0) {
                photos = photoStockApi.search(keywordToSearch.getText());
                photoTable.revalidate();
                photoTable.repaint();
            }
        }

    }
    
    private class StockPhotoTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return photos.size() / getColumnCount() + 
                    photos.size() % getColumnCount();
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
                Photo photo = (Photo) photoTable.getModel().getValueAt(rowIndex, columnIndex);
                keywordsList.setModel(new KeywordListModel(photo));
                keywordsList.revalidate();
                keywordsList.repaint();
                photoTable.changeSelection(rowIndex, columnIndex, false, false);
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
            }
        }
        
    }
        
}
