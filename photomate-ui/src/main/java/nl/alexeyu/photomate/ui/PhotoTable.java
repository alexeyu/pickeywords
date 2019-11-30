package nl.alexeyu.photomate.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

final class PhotoTable<P extends AbstractPhoto> extends JTable implements PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger();

    private static final int CELL_HEIGHT = UiConstants.THUMBNAIL_SIZE.height + 16;

    private static final int BOTTOM_LEFT_X_SELECTING_PHOTO = 50;

    private static final int BOTTOM_LEFT_Y_SELECTING_PHOTO = 30;

    private final int columnCount;

    private final List<PhotoObserver<? super P>> observers = new ArrayList<>();

    private final List<Boolean> selected = new ArrayList<>();

    private Consumer<P> highlightedPhotoConsumer = p -> {};

    public PhotoTable(int columnCount) {
        this.columnCount = columnCount;
        init();
    }

    public PhotoTable(int columnCount, JComponent parent) {
        this(columnCount);
        injectIntoParent(parent);
    }

    public void setHighlightedPhotoConsumer(Consumer<P> consumer) {
        this.highlightedPhotoConsumer = consumer;
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

    private void invertPhotoSelection(int x, int y) {
        logger.debug("Click: x {}, width {}", x, getWidth());
        int relativeY = y % CELL_HEIGHT;
        if (x > getWidth() - BOTTOM_LEFT_X_SELECTING_PHOTO &&
                relativeY < BOTTOM_LEFT_Y_SELECTING_PHOTO) {
            int row = rowAtPoint(new Point(x, y));
            if (row < selected.size()) {
                selected.set(row, !selected.get(row));
                repaint();
            }
        }
    }

    private class PhotoMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                getActivePhoto().ifPresent(highlightedPhotoConsumer);
            } else {
                invertPhotoSelection(e.getX(), e.getY());
            }
        }
    }
}
