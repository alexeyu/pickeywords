package nl.alexeyu.photomate.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoMetaData;

public abstract class AbstractPhoto implements Photo {
	
    protected static final Logger logger = LogManager.getLogger();
    
    public static final String THUMBNAIL_PROPERTY = "thumbnail";

    public static final String METADATA_PROPERTY = "metadata";

    private final AtomicReference<PhotoMetaData> metaData = new AtomicReference<>(PhotoMetaData.EMPTY);
    
    private final AtomicReference<ImageIcon> thumbnail = new AtomicReference<>(new ImageIcon());
    
    private List<WeakReference<PropertyChangeListener>> listeners = new CopyOnWriteArrayList<>();

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(new WeakReference<>(listener));
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
    	listeners.removeIf(ref -> ref.get() == listener);
    }

    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        var event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        listeners.stream()
            .map(WeakReference::get)
            .filter(Objects::nonNull)
            .forEach(listener -> listener.propertyChange(event)); 
    }

    @Override
    public ImageIcon thumbnail() {
        return thumbnail.get();
    }

    @Override
    public PhotoMetaData metaData() {
        return metaData.get();
    }
    
    protected void addThumbnail(ImageIcon thumbnail) {
    	logger.debug("Setting thumbnail to {}", name());
    	this.thumbnail.set(thumbnail);
        firePropertyChange(THUMBNAIL_PROPERTY, null, thumbnail);
    }
    
    public final void setMetaData(PhotoMetaData newMetaData) {
        var oldMetaData = this.metaData.get();
        if (oldMetaData == null || !oldMetaData.equals(newMetaData)) {
            metaData.set(newMetaData);
            firePropertyChange(METADATA_PROPERTY, oldMetaData, metaData);
        }
    }
    
    public final Collection<String> keywords() {
    	return metaData.get().keywords();
    }

    @Override
    public int hashCode() {
    	return Objects.hash(name());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractPhoto))
            return false;
        var other = (Photo) obj;
        return name().equals(other.name());
    }

}
