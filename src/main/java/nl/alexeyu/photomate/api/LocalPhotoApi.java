package nl.alexeyu.photomate.api;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.LocalPhoto;
import nl.alexeyu.photomate.model.ResultProcessor;
import nl.alexeyu.photomate.service.TaskWeight;
import nl.alexeyu.photomate.service.ThumbnailProvider;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.keyword.KeywordProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPhotoApi extends AbstractPhotoApi<LocalPhoto> {
    
    private static final Logger logger = LoggerFactory.getLogger("LocalPhotoAPI");
    
    @Inject
    private KeywordProcessor keywordProcessor;

    @Inject
    private ExecutorService executor;
    
    @Inject 
    private ThumbnailProvider thumbnailProvider;

    @Override
    public void provideThumbnail(LocalPhoto photo, ResultProcessor<ImageIcon> filler) {
        executor.submit(
                new ThumbnailingTask(photo.getFile(), 
                        new ProxyResultProcessor<>("thumbnail", filler)));
    }

    @Override
    public void provideKeywords(LocalPhoto photo, ResultProcessor<List<String>> filler) {
        executor.submit(
                new ReadKeywordsTask(photo.getFile().getAbsolutePath(), 
                        new ProxyResultProcessor<>("keywords", filler)));
    }

    public void addKeywords(LocalPhoto photo, List<String> keywords) {
        photo.addKeywords(keywords);
        executor.submit(new AddKeywordTask(photo.getFile().getAbsolutePath(), keywords));
    }

    public void removeKeywords(LocalPhoto photo, List<String> keywords) {
        photo.removeKeywords(keywords);
        executor.submit(new RemoveKeywordTask(photo.getFile().getAbsolutePath(), keywords));
    }

    private class ThumbnailingTask implements WeighedTask, Callable<ImageIcon> {

        protected final File photoFile;
        
        private final ResultProcessor<ImageIcon> resultProcessor;
        
        public ThumbnailingTask(File photoFile, ResultProcessor<ImageIcon> resultProcessor) {
            this.photoFile = photoFile;
            this.resultProcessor = resultProcessor;
        }

        @Override
        public ImageIcon call() throws Exception {
            long time = System.currentTimeMillis();
            ImageIcon image = new ImageIcon(thumbnailProvider.getThumbnail(photoFile));
            logger.info("" + (System.currentTimeMillis() - time));
            resultProcessor.process(image);
            return image;
        }

        @Override
        public TaskWeight getWeight() {
            return TaskWeight.HEAVY;
        }

    }

    private abstract class AbstractKeywordTask implements WeighedTask {

        protected final String path;

        public AbstractKeywordTask(String path) {
            this.path = path;
        }

        @Override
        public TaskWeight getWeight() {
            return TaskWeight.LIGHT;
        }

    }

    private class ReadKeywordsTask extends AbstractKeywordTask implements Runnable {
        
        private final ResultProcessor<List<String>> filler;

        public ReadKeywordsTask(String path, ResultProcessor<List<String>> filler) {
            super(path);
            this.filler = filler;
        }

        public void run() {
            List<String> keywords = keywordProcessor.readKeywords(path);
            filler.process(keywords);
        }

    }

    private class AddKeywordTask extends AbstractKeywordTask implements Runnable {

        private final List<String> keywords;

        public AddKeywordTask(String path, List<String> keywords) {
            super(path);
            this.keywords = keywords;
        }

        public void run() {
            keywordProcessor.addKeywords(path, keywords);
        }

    }

    private class RemoveKeywordTask extends AbstractKeywordTask implements Runnable {

        private final List<String> keywords;

        public RemoveKeywordTask(String path, List<String> keywords) {
            super(path);
            this.keywords = keywords;
        }

        public void run() {
            keywordProcessor.removeKeywords(path, keywords);
        }

    }

}
