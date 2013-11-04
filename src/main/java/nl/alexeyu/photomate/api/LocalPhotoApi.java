package nl.alexeyu.photomate.api;

import java.awt.Image;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.DefaultPhotoMetaData;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.service.PrioritizedTask;
import nl.alexeyu.photomate.service.keyword.PhotoMetadataProcessor;
import nl.alexeyu.photomate.service.thumbnail.ThumbnailProvider;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPhotoApi implements PhotoApi<LocalPhoto> {
    
    private static final Logger logger = LoggerFactory.getLogger("LocalPhotoAPI");
    
    @Inject
    private PhotoMetadataProcessor metadataProcessor;

    @Inject
    private ExecutorService executor;
    
    @Inject 
    private ThumbnailProvider thumbnailProvider;

    @Override
    public void provideThumbnail(LocalPhoto photo) {
        executor.submit(new ThumbnailingTask(photo));
    }

    @Override
    public void provideMetadata(LocalPhoto photo) {
        executor.submit(new ReadMetadataTask(photo));
    }

    public void updateKeywords(LocalPhoto photo, List<String> keywords) {
        PhotoMetaData old = photo.getMetaData();
        PhotoMetaData metaData = new DefaultPhotoMetaData(
                old.getCaption(), 
                old.getDescription(), 
                old.getCreator(), 
                keywords);
        executor.submit(new UpdateMetaDataTask(photo, metaData));
    }

    public void updateCaption(LocalPhoto photo, String caption) {
        PhotoMetaData old = photo.getMetaData();
        PhotoMetaData metaData = new DefaultPhotoMetaData(
                caption, 
                old.getDescription(), 
                old.getCreator(), 
                old.getKeywords());
        executor.submit(new UpdateMetaDataTask(photo, metaData));
    }

    public void updateDescription(LocalPhoto photo, String description) {
        PhotoMetaData old = photo.getMetaData();
        PhotoMetaData metaData = new DefaultPhotoMetaData(
                old.getCaption(), 
                description, 
                old.getCreator(), 
                old.getKeywords());
        executor.submit(new UpdateMetaDataTask(photo, metaData));
    }

    public void updateCreator(LocalPhoto photo, String creator) {
        PhotoMetaData old = photo.getMetaData();
        PhotoMetaData metaData = new DefaultPhotoMetaData(
                old.getCaption(), 
                old.getDescription(), 
                creator, 
                old.getKeywords());
        executor.submit(new UpdateMetaDataTask(photo, metaData));
    }

    private class ThumbnailingTask implements PrioritizedTask, Runnable {

        private final LocalPhoto photo;
        
        public ThumbnailingTask(LocalPhoto photo) {
            this.photo = photo;
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis();
            Pair<Image, Image> images = thumbnailProvider.getThumbnails(photo.getFile());
            photo.setThumbnail(new ImageIcon(images.getLeft()));
            photo.setPreview(new ImageIcon(images.getRight()));
            logger.info("" + (System.currentTimeMillis() - time));
        }

        @Override
        public TaskPriority getPriority() {
            return TaskPriority.MEDIUM;
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

    }

    private class ReadMetadataTask extends AbstractMetadataTask implements Runnable {
        
        public ReadMetadataTask(LocalPhoto photo) {
            super(photo);
        }
        
        @Override
        protected DefaultPhotoMetaData processMetaData() {
            return metadataProcessor.read(photo.getFile().getAbsolutePath());
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
            metadataProcessor.update(photo.getFile().getAbsolutePath(), photo.getMetaData(), metaData);
            return metaData;
        }

        @Override
        public TaskPriority getPriority() {
            return TaskPriority.MEDIUM;
        }

    }

}
