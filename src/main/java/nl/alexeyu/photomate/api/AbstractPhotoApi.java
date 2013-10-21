package nl.alexeyu.photomate.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.ResultProcessor;

public abstract class AbstractPhotoApi<P extends Photo> implements PhotoApi<P> {

    protected List<PropertyChangeListener> listeners = new ArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    protected class ProxyResultProcessor<T> implements ResultProcessor<T> {
        
        private final String propertyName;

        private final ResultProcessor<T> resultProcessor;
        
        public ProxyResultProcessor(String propertyName, ResultProcessor<T> resultProcessor) {
            this.propertyName = propertyName;
            this.resultProcessor = resultProcessor;
        }

        @Override
        public void process(T result) {
            resultProcessor.process(result);
            PropertyChangeEvent event = new PropertyChangeEvent(AbstractPhotoApi.this, propertyName, null, result);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
        
    }


}