package nl.alexeyu.photomate.service.keyword;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.TaskWeight;
import nl.alexeyu.photomate.service.UpdateListener;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExifKeywordReader implements KeywordReader {
	
	private final Logger logger = LoggerFactory.getLogger("ExifKeywordReader");
	
	private ExecutorService executor;
	
	private UpdateListener<Photo> listener;
	
	public void readKeywords(Photo photo) {
		executor.execute(new ReadKeywordsTask(photo));
	}

	public void addKeyword(Photo photo, String keyword) {
		photo.addKeyword(keyword);
		executor.execute(new AddKeywordTask(photo, keyword));
	}

	public void removeKeyword(Photo photo, String keyword) {
		photo.removeKeyword(keyword);
		executor.execute(new RemoveKeywordTask(photo, keyword));
	}

	public void setListener(UpdateListener<Photo> listener) {
		this.listener = listener;
	}

	@Inject
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	private String execExif(String args, Photo photo) {
		try {
			String filePath = photo.getFile().getAbsolutePath();
			String[] execArgs = new String[] {"exiftool", args, filePath};
			Process p = Runtime.getRuntime().exec(execArgs);
			try (InputStream is = p.getInputStream()) {
				if (listener != null) {
					listener.onUpdate(photo);
				}
				return IOUtils.toString(is);
			}
		} catch (IOException ex) {
			logger.error("Could not run exif", ex);
			return "";
		}
	}
	
	private abstract class AbstractKeywordTask implements WeighedTask {

		protected final Photo photo;
		
		public AbstractKeywordTask(Photo photo) {
			this.photo = photo;
		}

		@Override
		public TaskWeight getWeight() {
			return TaskWeight.LIGHT;
		}
		
	}

	private class ReadKeywordsTask extends AbstractKeywordTask {
		
		public ReadKeywordsTask(Photo photo) {
			super(photo);
		}

		public void run() {
			photo.setKeywords(readKeywordsImpl());
		}

		public List<String> readKeywordsImpl() {
			List<String> keywords = new ArrayList<String>();
			String result = execExif("-keywords", photo);
			int columnPos = result.indexOf(":");
			if (columnPos > 0) {
				String keywordsLine = result.substring(columnPos + 1);
				StringTokenizer tokenizer = new StringTokenizer(keywordsLine, ",");
				while (tokenizer.hasMoreTokens()) {
					keywords.add(tokenizer.nextToken());
				}
			}
			return keywords;
		}

	}

	private class AddKeywordTask extends AbstractKeywordTask {
		
		private final String keyword;

		public AddKeywordTask(Photo photo, String keyword) {
			super(photo);
			this.keyword = keyword;
		}

		public void run() {
			execExif("-keywords+=" + keyword, photo);
		}

	}
	
	private class RemoveKeywordTask extends AbstractKeywordTask {
		
		private final String keyword;

		public RemoveKeywordTask(Photo photo, String keyword) {
			super(photo);
			this.keyword = keyword;
		}

		public void run() {
			execExif("-keywords-=" + keyword, photo);
		}

	}

}
