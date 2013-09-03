package nl.alexeyu.photomate.service.keyword;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.UpdateListener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ExifKeywordReader implements KeywordReader {
	
	private final Logger logger = Logger.getLogger("ExifKeywordReader");
	
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

	@Autowired
	public void setExecutor(@Qualifier("lightTaskExecutor") ExecutorService executor) {
		this.executor = executor;
	}

	private String execExif(String args, Photo photo) {
		try {
			Process p = Runtime.getRuntime().exec("exiftool " + args + " " + photo.getFile().getAbsolutePath());
			try (InputStream is = p.getInputStream()) {
				if (listener != null) {
					listener.onUpdate(photo);
				}
				return IOUtils.toString(is);
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Could not run exif", ex);
			return "";
		}
	}
	
	private abstract class AbstractTask implements Runnable {

		protected final Photo photo;
		
		public AbstractTask(Photo photo) {
			this.photo = photo;
		}
		
	}

	private class ReadKeywordsTask extends AbstractTask {
		
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
				String[] keywordsArray = StringUtils.split(keywordsLine, ",");
				for (String kw : keywordsArray) {
					keywords.add(kw.trim());
				}
			}
			return keywords;
		}

	}

	private class AddKeywordTask extends AbstractTask {
		
		private final String keyword;

		public AddKeywordTask(Photo photo, String keyword) {
			super(photo);
			this.keyword = keyword;
		}

		public void run() {
			execExif("-keywords+=" + keyword, photo);
		}

	}
	
	private class RemoveKeywordTask extends AbstractTask {
		
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
