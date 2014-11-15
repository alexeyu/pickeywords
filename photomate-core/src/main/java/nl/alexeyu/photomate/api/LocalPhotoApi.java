package nl.alexeyu.photomate.api;

import static nl.alexeyu.photomate.service.PrioritizedTask.TaskPriority.MEDIUM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.model.PhotoProperty;
import nl.alexeyu.photomate.service.PrioritizedTask;
import nl.alexeyu.photomate.service.metadata.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.thumbnail.ThumbnailProvider;
import nl.alexeyu.photomate.service.thumbnail.Thumbnails;
import nl.alexeyu.photomate.util.ImageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LocalPhotoApi<P extends LocalPhoto> implements PhotoApi<P> {
    
    private static final Logger logger = LoggerFactory.getLogger("LocalPhotoAPI");
    
    @Inject
    private PhotoMetadataProcessor metadataProcessor;

    @Inject
    private ExecutorService commonExecutor;
    
    // Updates should be serial per photo. Although for simplicity it is OK to make them wholly serial.
    private ExecutorService updateExecutor = Executors.newSingleThreadExecutor(); 
    
    @Inject 
    private ThumbnailProvider thumbnailProvider;
    
    public List<P> createPhotos(Path dir) {
    	try {
	    	List<P> photos = Files.list(dir)
	    			.filter(path -> ImageUtils.isJpeg(path))
	    			.map(path -> createPhoto(path))
	    			.collect(Collectors.toList());
	    	photos.forEach(photo -> init(photo));
	    	return photos;
    	} catch (IOException ex) {
    		throw new IllegalStateException(ex);
    	}
    }

    protected abstract P createPhoto(Path path);
    
    @Override
    public void provideThumbnail(P photo) {
    	commonExecutor.execute(new ThumbnailingTask(photo));
    }

    @Override
    public void provideMetadata(LocalPhoto photo) {
    	commonExecutor.execute(new ReadMetadataTask(photo));
    }

    public void updateProperty(LocalPhoto photo, String propertyName, Object propertyValue) {
        Map<PhotoProperty, Object> newProps = new HashMap<>();
        for (PhotoProperty pp : PhotoProperty.values()) {
        	newProps.put(pp, photo.getMetaData().getProperty(pp));
        }
        newProps.put(PhotoProperty.of(propertyName), propertyValue);
        PhotoMetaData metaData = new DefaultPhotoMetaData(newProps);
        updateExecutor.execute(new UpdateMetaDataTask(photo, metaData));
    }
    
    protected void setThumbnails(P photo, Thumbnails images) {
    	photo.setThumbnail(new ImageIcon(images.getThumbnail()));
    }

    private class ThumbnailingTask implements PrioritizedTask, Runnable {

        private final P photo;
        
        public ThumbnailingTask(P photo) {
            this.photo = photo;
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis();
            Thumbnails images = thumbnailProvider.getThumbnails(photo.getPath(), photo.hasPreview());
            logger.info("" + (System.currentTimeMillis() - time));
            setThumbnails(photo, images);
        }

        @Override
        public TaskPriority getPriority() {
            return MEDIUM;
        }

		@Override
		public String toString() {
			return "ThumbnailingTask (" + getPriority() + ")";
		}

        
    }

    private abstract class AbstractMetadataTask implements PrioritizedTask, Runnable {

        protected final LocalPhoto photo;

        public AbstractMetadataTask(LocalPhoto photo) {
            this.photo = photo;
        }

        protected abstract PhotoMetaData processMetaData();
        
        @Override
        public final void run() {
            PhotoMetaData metaData = processMetaData();
            photo.setMetaData(metaData);
        }
        
        public String toString() {
        	return getClass().getSimpleName() + " (" + getPriority() + ")";
        }

    }

    private class ReadMetadataTask extends AbstractMetadataTask implements Runnable {
        
        public ReadMetadataTask(LocalPhoto photo) {
            super(photo);
        }
        
        @Override
        protected PhotoMetaData processMetaData() {
            return metadataProcessor.read(photo.getPath());
        }

        @Override
        public TaskPriority getPriority() {
            return TaskPriority.HIGH;
        }

    }

    private class UpdateMetaDataTask extends AbstractMetadataTask {

        private final PhotoMetaData metaData;

        public UpdateMetaDataTask(LocalPhoto photo, PhotoMetaData metaData) {
            super(photo);
            this.metaData = metaData;
        }

        @Override
        protected PhotoMetaData processMetaData() {
            metadataProcessor.update(photo.getPath(), photo.getMetaData(), metaData);
            return metaData;
        }

        @Override
        public TaskPriority getPriority() {
            return TaskPriority.MEDIUM;
        }

    }

}
