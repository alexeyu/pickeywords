package nl.alexeyu.photomate.ui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public class PhotoTable<P extends AbstractPhoto> extends JTable implements PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger();

    private static final int CELL_HEIGHT = UiConstants.THUMBNAIL_SIZE.height + 16;

    private static final int BOTTOM_LEFT_X_SELECTING_PHOTO = 50;

    private static final int BOTTOM_LEFT_Y_SELECTING_PHOTO = 30;

    private final int columnCount;

    private final List<PhotoObserver<? super P>> observers = new ArrayList<>();

    private List<Boolean> selected = new ArrayList<>();

    public PhotoTable(int columnCount) {
        this.columnCount = columnCount;
        init();
    }

    public PhotoTable(int columnCount, JComponent parent) {
        this(columnCount);
        injectIntoParent(parent);
    }

    private void init() {
        setPhotos(List.of());
        setDefaultRenderer(Object.class, new PhotoCellRenderer(idx -> selected.isEmpty() ? false : selected.get(idx)));

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
        ListSelectionListener selectionListener = new SelectionListener();
        getSelectionModel().addListSelectionListener(selectionListener);
        getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);

        setRowHeight(CELL_HEIGHT);
        setPreferredScrollableViewportSize(UiConstants.THUMBNAIL_SIZE);

        addMouseListener(new PhotoMouseListener());
    }

    private void injectIntoParent(JComponent parent) {
        var sp = new JScrollPane();
        sp.setViewportView(this);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getViewport().setBackground(getBackground());

        getTableHeader().setPreferredSize(new Dimension(0, 0));
        getTableHeader().setVisible(false);

        parent.add(sp);
    }

    public void addObserver(PhotoObserver<? super P> observer) {
        this.observers.add(observer);
    }

    public void setPhotos(List<P> photos) {
        selected.clear();
        photos.forEach(photo -> selected.add(false));
        photos.forEach(photo -> photo.addPropertyChangeListener(this));
        var model = new PhotoTableModel<>(photos, columnCount);
        setModel(model);
    }

    @SuppressWarnings("unchecked")
    public PhotoTableModel<P> getModel() {
        if (super.getModel() instanceof PhotoTableModel) {
            return (PhotoTableModel<P>) super.getModel();
        }
        return null;
    }

    public Optional<P> getActivePhoto() {
        return getModel().getValueAt(getSelectedRow(), getSelectedColumn());
    }

    public List<P> getSelectedPhotos() {
        List<P> result = new ArrayList<>();
        for (int index = 0; index < getModel().getRowCount(); index++) {
            if (selected.get(index)) {
                Optional<P> photo = getModel().getValueAt(index, 0);
                photo.ifPresent(result::add);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        SwingUtilities.invokeLater(() -> repaint());
    }

    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            var photo = getActivePhoto().orElse(null);
            observers.forEach(observer -> observer.photoSelected(photo));
        }

    }

    private class PhotoMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            logger.debug("Click: x {}, width {}", e.getX(), PhotoTable.this.getWidth());
            int relativeY = e.getY() % CELL_HEIGHT;
            if (e.getX() > PhotoTable.this.getWidth() - BOTTOM_LEFT_X_SELECTING_PHOTO &&
                    relativeY < BOTTOM_LEFT_Y_SELECTING_PHOTO) {
                int row = e.getY() / CELL_HEIGHT;
                if (row < selected.size()) {
                    selected.set(row, !selected.get(row));
                    PhotoTable.this.repaint();
                }
            }
        }
    }
}
