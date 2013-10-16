package nl.alexeyu.photomate.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import nl.alexeyu.photomate.model.ResultFiller;

public abstract class AbstractPhotoApi implements PhotoApi {

    protected List<PropertyChangeListener> listeners = new ArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    protected class ProxyFiller<T> implements ResultFiller<T> {
        
        private final String propertyName;

        private final ResultFiller<T> filler;
        
        public ProxyFiller(String propertyName, ResultFiller<T> filler) {
            this.propertyName = propertyName;
            this.filler = filler;
        }

        @Override
        public void fill(T result) {
            filler.fill(result);
            PropertyChangeEvent event = new PropertyChangeEvent(AbstractPhotoApi.this, propertyName, null, result);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
        
    }


}