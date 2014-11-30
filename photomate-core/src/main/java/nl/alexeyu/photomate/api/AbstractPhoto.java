package nl.alexeyu.photomate.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoMetaData;

public abstract class AbstractPhoto implements Photo {
    
    public static final String THUMBNAIL_PROPERTY = "thumbnail";

    public static final String METADATA_PROPERTY = "metadata";

    protected final AtomicReference<PhotoMetaData> metaData = new AtomicReference<>();
    
    private final AtomicReference<ImageIcon> thumbnail = new AtomicReference<>();
    
    protected List<WeakReference<PropertyChangeListener>> listeners = new CopyOnWriteArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(new WeakReference<PropertyChangeListener>(listener));
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	listeners.removeIf(ref -> ref.get() == listener);
    }

    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        listeners.forEach(ref -> ref.get().propertyChange(event)); 
    }

    @Override
    public ImageIcon getThumbnail() {
        return thumbnail.get();
    }

    public int getThumbnailCount() {
    	return 1;
    }
    
    public ImageIcon getThumbnail(int index) {
    	if (index > 0) {
    		throw new IllegalArgumentException("Only one thumbnail is allowed");
    	}
    	return getThumbnail();
    }
    
    @Override
    public PhotoMetaData getMetaData() {
        return metaData.get();
    }

    public void addThumbnail(ImageIcon thumbnail) {
    	this.thumbnail.set(thumbnail);
        firePropertyChanged(THUMBNAIL_PROPERTY, null, thumbnail);
    }
    
    public void setMetaData(PhotoMetaData metaData) {
        PhotoMetaData oldMetaData = this.metaData.getAndSet(metaData);
        firePropertyChanged(METADATA_PROPERTY, oldMetaData, metaData);
    }
    
    @Override
    public int hashCode() {
    	return Objects.hash(getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractPhoto))
            return false;
        Photo other = (Photo) obj;
        return getName().equals(other.getName());
    }

}
