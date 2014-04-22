package nl.alexeyu.photomate.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import nl.alexeyu.photomate.model.PhotoStock;

import org.apache.commons.io.IOUtils;

@Singleton
public class ConfigReader {
	
	private static final String CONFIG_LOCATION_SYS_PROP = "configfile";
	
	private static final String DEFAULT_CONFIG_FILE = "/photomate.properties";
	
	private static final Pattern PHOTO_STOCK_NAME = Pattern.compile("stock\\.([\\w]+)\\.name");
	
	private Properties properties = new Properties();
	
	private List<PhotoStock> photoStocks;
	
	public ConfigReader() {
		InputStream is = null;
		try {
			String location = System.getProperty(CONFIG_LOCATION_SYS_PROP);
			if (location != null) {
				is = new FileInputStream(new File(location));
			} else {
				is = getClass().getResourceAsStream(DEFAULT_CONFIG_FILE);
			}
			properties.load(is);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		} finally {
			IOUtils.closeQuietly(is);
		}
		photoStocks = readPhotoStocks();
	}

	private List<PhotoStock> readPhotoStocks() {
		return properties.stringPropertyNames().stream()
				.filter(prop -> !prop.startsWith("#"))
				.map(prop -> PHOTO_STOCK_NAME.matcher(prop))
				.filter(matcher -> matcher.matches())
				.map(matcher -> readPhotoStock(matcher.group(1)))
				.collect(Collectors.toList());
	}

	private PhotoStock readPhotoStock(String key) {
		String prefix = "stock." + key + ".";
		String name = properties.getProperty(prefix + "name", "");
		String icon = properties.getProperty(prefix + "icon", "");
		String ftpUrl = properties.getProperty(prefix + "ftp.url", "");
		String ftpUsername = properties.getProperty(prefix + "ftp.username", "");
		String ftpPassword = properties.getProperty(prefix + "ftp.password", "");
		return new PhotoStock(name, icon, ftpUrl, ftpUsername, ftpPassword);
	}
	
	public List<PhotoStock> getPhotoStocks() {
		return photoStocks;
	}
	
	public String getProperty(String property, String defaultValue) {
		return properties.getProperty(property, defaultValue);
	}
}
