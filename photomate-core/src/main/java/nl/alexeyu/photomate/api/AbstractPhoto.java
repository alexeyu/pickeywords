package nl.alexeyu.photomate.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoMetaData;

public abstract class AbstractPhoto implements Photo {
    
    public static final String THUMBNAIL_PROPERTY = "thumbnail";

    public static final String METADATA_PROPERTY = "metadata";

    protected final AtomicReference<PhotoMetaData> metaData = new AtomicReference<>();
    
    private final AtomicReference<ImageIcon> thumbnail = new AtomicReference<>();
    
    protected Map<WeakReference<PropertyChangeListener>, Object> listeners = new ConcurrentHashMap<>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.put(new WeakReference<PropertyChangeListener>(listener), StringUtils.EMPTY);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        Iterator<WeakReference<PropertyChangeListener>> it = listeners.keySet().iterator();
        while (it.hasNext()) {
            WeakReference<PropertyChangeListener> ref = it.next();
            if (ref.get() == listener) {
                it.remove();
                break;
            }
        }
    }

    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        listeners.keySet().forEach((listener) -> listener.get().propertyChange(event)); 
    }

    @Override
    public ImageIcon getThumbnail() {
        return thumbnail.get();
    }

    @Override
    public PhotoMetaData getMetaData() {
        return metaData.get();
    }

    public void setThumbnail(ImageIcon thumbnail) {
        if (!this.thumbnail.compareAndSet(null, thumbnail)) {
            throw new IllegalStateException("Attempt to set thumbnail 2nd time");
        }
        firePropertyChanged(THUMBNAIL_PROPERTY, null, thumbnail);
    }
    
    public void setMetaData(PhotoMetaData metaData) {
        PhotoMetaData oldMetaData = this.metaData.getAndSet(metaData);
        firePropertyChanged(METADATA_PROPERTY, oldMetaData, metaData);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getName().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Photo other = (Photo) obj;
        return getName().equals(other.getName());
    }

}
