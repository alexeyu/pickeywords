package nl.alexeyu.photomate.model;

import java.util.Collection;
import java.util.Collections;

public interface PhotoMetaData {
	
	PhotoMetaData EMPTY = new EmptyPhotoMetaData();
	
	default boolean isEmpty() {
		return this == EMPTY;
	}

    Object getProperty(PhotoProperty p);

    Collection<String> keywords();
    
    String description();
    
    String caption();
    
    static class EmptyPhotoMetaData implements PhotoMetaData {

		@Override
		public Object getProperty(PhotoProperty p) {
			return "";
		}

		@Override
		public Collection<String> keywords() {
			return Collections.emptyList();
		}

		@Override
		public String description() {
			return "";
		}

		@Override
		public String caption() {
			return "";
		}
    	
    	
    }

}