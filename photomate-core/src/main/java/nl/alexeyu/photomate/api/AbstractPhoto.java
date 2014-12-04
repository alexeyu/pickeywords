package nl.alexeyu.photomate.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(new WeakReference<PropertyChangeListener>(listener));
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
    	listeners.removeIf(ref -> ref.get() == listener);
    }

    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        listeners.forEach(ref -> ref.get().propertyChange(event)); 
    }

    @Override
    public Optional<ImageIcon> thumbnail() {
        return Optional.ofNullable(thumbnail.get());
    }

    public int getThumbnailCount() {
    	return 1;
    }
    
    @Override
    public Optional<PhotoMetaData> metaData() {
        return Optional.ofNullable(metaData.get());
    }

    public void addThumbnail(ImageIcon thumbnail) {
    	this.thumbnail.set(thumbnail);
        firePropertyChange(THUMBNAIL_PROPERTY, null, thumbnail);
    }
    
    public final void setMetaData(PhotoMetaData metaData) {
        PhotoMetaData oldMetaData = this.metaData.getAndSet(metaData);
        firePropertyChange(METADATA_PROPERTY, oldMetaData, metaData);
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
        Photo other = (Photo) obj;
        return name().equals(other.name());
    }

}
