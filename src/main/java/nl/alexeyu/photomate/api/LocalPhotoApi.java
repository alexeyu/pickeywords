package nl.alexeyu.photomate.api;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.ResultFiller;
import nl.alexeyu.photomate.service.TaskWeight;
import nl.alexeyu.photomate.service.ThumbnailProvider;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.keyword.KeywordProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPhotoApi extends AbstractPhotoApi {
    
    private static final Logger logger = LoggerFactory.getLogger("LocalPhotoAPI");
    
    @Inject
    private KeywordProcessor keywordProcessor;

    @Inject
    private ExecutorService executor;
    
    @Inject 
    private ThumbnailProvider thumbnailProvider;

    @Override
    public void provideThumbnail(String photoPath, ResultFiller<ImageIcon> filler) {
        executor.submit(
                new ThumbnailingTask(photoPath, 
                        new ProxyFiller<>("thumbnail", filler)));
    }

    @Override
    public void provideKeywords(String photoPath, ResultFiller<List<String>> filler) {
        executor.submit(
                new ReadKeywordsTask(photoPath, 
                        new ProxyFiller<>("keywords", filler)));
    }

    public void addKeyword(String photoPath, String keyword) {
        executor.submit(new AddKeywordTask(photoPath, keyword));
    }

    public void removeKeyword(String photoPath, String keyword) {
        executor.submit(new RemoveKeywordTask(photoPath, keyword));
    }

    private class ThumbnailingTask implements WeighedTask, Callable<ImageIcon> {

        protected final String photoPath;
        
        private final ResultFiller<ImageIcon> filler;
        
        public ThumbnailingTask(String photoPath, ResultFiller<ImageIcon> filler) {
            this.photoPath = photoPath;
            this.filler = filler;
        }

        @Override
        public ImageIcon call() throws Exception {
            long time = System.currentTimeMillis();
            ImageIcon image = new ImageIcon(thumbnailProvider.getThumbnail(photoPath));
            logger.info("" + (System.currentTimeMillis() - time));
            filler.fill(image);
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
        
        private final ResultFiller<List<String>> filler;

        public ReadKeywordsTask(String path, ResultFiller<List<String>> filler) {
            super(path);
            this.filler = filler;
        }

        public void run() {
            List<String> keywords = keywordProcessor.readKeywords(path);
            filler.fill(keywords);
        }

    }

    private class AddKeywordTask extends AbstractKeywordTask implements Runnable {

        private final String keyword;

        public AddKeywordTask(String path, String keyword) {
            super(path);
            this.keyword = keyword;
        }

        public void run() {
            keywordProcessor.addKeyword(path, keyword);
        }

    }

    private class RemoveKeywordTask extends AbstractKeywordTask implements Runnable {

        private final String keyword;

        public RemoveKeywordTask(String path, String keyword) {
            super(path);
            this.keyword = keyword;
        }

        public void run() {
            keywordProcessor.removeKeyword(path, keyword);
        }

    }

}
