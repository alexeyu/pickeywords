package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.model.Photo;

import com.google.inject.Inject;

public class PhotoStockPanel extends JPanel {
    
    private JTextField keywordToSearch;

    private JList<String> keywordsList;

    private JTable photoTable;
    
    private JButton searchButton;
    
    @Inject
    private PhotoStockApi photoStockApi;
    
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
        
        photoTable.getSelectionModel().addListSelectionListener(new PhotoSelectionListener());
        
        keywordsList = new JList();
        keywordsList.setPreferredSize(new Dimension(250, 0));
        
        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(keywordsList), BorderLayout.WEST);
        add(new JScrollPane(photoTable), BorderLayout.CENTER);
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

    private class PhotoSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getFirstIndex() >= 0) {
                Photo photo = photos.get(e.getFirstIndex());
                keywordsList.setModel(new KeywordListModel(photo));
            }
        }
        
    }
}
