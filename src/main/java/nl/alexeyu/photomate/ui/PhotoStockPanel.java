package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import nl.alexeyu.photomate.model.StockPhotoDescription;
import nl.alexeyu.photomate.service.PhotoSearcher;

import com.google.inject.Inject;

public class PhotoStockPanel extends JPanel {
    
    private static final int MAX_PHOTOS = 10;

    private JTextField keywordToSearch;

    private JList<String> keywordsList;

    private JTable photoTable;
    
    private JButton searchButton;
    
    @Inject
    private PhotoSearcher photoSearcher;
    
    private List<StockPhotoDescription> photoDescriptions = new ArrayList<>();
    
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
                    List<StockPhotoDescription> photos = photoSearcher.search(keywordToSearch.getText());
                    int count = Math.min(photos.size(), MAX_PHOTOS);
                    photoDescriptions = photos.subList(0, count);
                    photoTable.revalidate();
                    photoTable.repaint();
                }
            }

    }
    
    private class StockPhotoTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return photoDescriptions.size() / getColumnCount() + 
                    photoDescriptions.size() % getColumnCount();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int index = rowIndex * getColumnCount() + columnIndex; 
            if (index >= photoDescriptions.size()) {
                return null;
            }
            return photoDescriptions.get(index);
        }
        
    }
    
    private class PhotoCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label;
            if (value instanceof StockPhotoDescription) {
                label = new JLabel(photoSearcher.getIcon((StockPhotoDescription) value));
                if (hasFocus) {
                    label.setBorder(Constants.LINE_BORDER);
                }
            } else {
                label = new JLabel();
            }
            return label;
        }
        
    }
}
