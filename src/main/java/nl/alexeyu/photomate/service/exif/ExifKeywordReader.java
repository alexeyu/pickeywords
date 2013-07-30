package nl.alexeyu.photomate.service.exif;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.ExecutorServices;
import nl.alexeyu.photomate.service.KeywordReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ExifKeywordReader implements KeywordReader {
	
	private final Logger logger = Logger.getLogger("ExifKeywordReader");
	
	public void readKeywords(Photo photo) {
		getExecutor().execute(new ReadKeywordsTask(this, photo));
	}

	public void addKeyword(Photo photo, String keyword) {
		if (photo != Photo.NULL_PHOTO) {
			photo.addKeyword(keyword);
			getExecutor().execute(new AddKeywordTask(this, photo, keyword));
		}
	}

	public void removeKeyword(Photo photo, String keyword) {
		if (photo != Photo.NULL_PHOTO) {
			photo.removeKeyword(keyword);
			getExecutor().execute(new RemoveKeywordTask(this, photo, keyword));
		}
	}
	
	private ExecutorService getExecutor() {
		return ExecutorServices.getLightTasksExecutor();
	}

	private String execExif(String args, File file) {
		try {
			Process p = Runtime.getRuntime().exec("exiftool " + args + " " + file.getAbsolutePath());
			try (InputStream is = p.getInputStream()) {
				return IOUtils.toString(is);
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Could not run exif", ex);
			return "";
		}
	}
	
	private void addKeyword(File file, String keyword) {
		execExif("-keywords+=" + keyword, file);
	}
	
	private void removeKeyword(File file, String keyword) {
		execExif("-keywords-=" + keyword, file);
	}

	public List<String> readKeywords(File file) {
		List<String> keywords = new ArrayList<String>();
		String result = execExif("-keywords", file);
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

	private static abstract class AbstractTask implements Runnable {

		protected final WeakReference<ExifKeywordReader> keywoardReader;
		
		protected final Photo photo;
		
		public AbstractTask(ExifKeywordReader keywoardReader, Photo photo) {
			this.keywoardReader = new WeakReference<ExifKeywordReader>(keywoardReader);
			this.photo = photo;
		}
		
	}

	private static class ReadKeywordsTask extends AbstractTask {
		
		public ReadKeywordsTask(ExifKeywordReader keywoardReader, Photo photo) {
			super(keywoardReader, photo);
		}

		public void run() {
			photo.setKeywords(
				keywoardReader.get().readKeywords(photo.getFile()));
		}

	}

	private static class AddKeywordTask extends AbstractTask {
		
		private final String keyword;

		public AddKeywordTask(ExifKeywordReader keywoardReader, Photo photo, String keyword) {
			super(keywoardReader, photo);
			this.keyword = keyword;
		}

		public void run() {
			keywoardReader.get().addKeyword(photo.getFile(), keyword);
		}

	}
	
	private static class RemoveKeywordTask extends AbstractTask {
		
		private final String keyword;

		public RemoveKeywordTask(ExifKeywordReader keywoardReader, Photo photo, String keyword) {
			super(keywoardReader, photo);
			this.keyword = keyword;
		}

		public void run() {
			keywoardReader.get().removeKeyword(photo.getFile(), keyword);
		}

	}

}
