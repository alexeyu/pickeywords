package nl.alexeyu.photomate.ui;

import java.awt.Dimension;
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

import nl.alexeyu.photomate.api.AbstractPhoto;
import nl.alexeyu.photomate.service.PhotoObserver;

public class PhotoTable<P extends AbstractPhoto> extends JTable implements PropertyChangeListener {

    private static final int CELL_HEIGHT = UiConstants.THUMBNAIL_SIZE.height + 16;

    private final int columnCount;

    private final List<PhotoObserver<? super P>> observers = new ArrayList<>();

    public PhotoTable(int columnCount) {
        this.columnCount = columnCount;
        init();
    }

    public PhotoTable(int columnCount, JComponent parent) {
        this(columnCount);
        injectIntoParent(parent);
    }

    private void init() {
        setPhotos(Collections.emptyList());
        setDefaultRenderer(Object.class, new PhotoCellRenderer());

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
        ListSelectionListener selectionListener = new SelectionListener();
        getSelectionModel().addListSelectionListener(selectionListener);
        getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);

        setRowHeight(CELL_HEIGHT);
        setPreferredScrollableViewportSize(UiConstants.THUMBNAIL_SIZE);
    }

    private void injectIntoParent(JComponent parent) {
        JScrollPane sp = new JScrollPane();
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
        photos.forEach(photo -> photo.addPropertyChangeListener(this));
        PhotoTableModel<P> model = new PhotoTableModel<>(photos, columnCount);
        setModel(model);
    }

    @SuppressWarnings("unchecked")
    public PhotoTableModel<P> getModel() {
        if (super.getModel() instanceof PhotoTableModel) {
            return (PhotoTableModel<P>) super.getModel();
        }
        return null;
    }

    public Optional<P> getSelectedPhoto() {
        return getModel().getValueAt(getSelectedRow(), getSelectedColumn());
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        SwingUtilities.invokeLater(() -> repaint());
    }

    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            Optional<P> photo = getSelectedPhoto();
            observers.forEach(observer -> observer.photoSelected(photo.orElse(null)));
        }

    }

}
