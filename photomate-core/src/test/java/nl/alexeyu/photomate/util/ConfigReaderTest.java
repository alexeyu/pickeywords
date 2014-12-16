package nl.alexeyu.photomate.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;

import nl.alexeyu.photomate.model.PhotoStock;

import org.junit.Before;
import org.junit.Test;

public class ConfigReaderTest {
	
	private ConfigReader configReader;
	
	private Properties properties;
	
	@Before
	public void setUp() {
		properties = new Properties();
		properties.put("stock.test.name", "test-stock");
		properties.put("stock.test.icon", "http://test.com/stock.gif");
		properties.put("stock.test.ftp.url", "ftp://test.com");
		properties.put("stock.test.ftp.username", "photographer");
		properties.put("stock.test.ftp.password", "#12");
		configReader = new ConfigReader(properties);
	}
	
	@Test
	public void readExistingProperty() {
		assertEquals("Cannot read a defined property", "test-stock", 
				configReader.getProperty("stock.test.name").get());
	}

	@Test
	public void readNotDefinedProperty() {
		assertFalse("Undefined property must not be available", 
				configReader.getProperty("undefined").isPresent());
	}
	
	@Test
	public void readPhotoStock() {
		List<PhotoStock> stocks = configReader.getPhotoStocks();
		assertEquals("Must read one stock", 1, stocks.size());
		assertEquals("test-stock", stocks.get(0).name());
		assertEquals("http://test.com/stock.gif", stocks.get(0).iconUrl());
		assertEquals("ftp://test.com", stocks.get(0).ftpUrl());
		assertEquals("photographer", stocks.get(0).ftpUsername());
		assertEquals("#12", stocks.get(0).ftpPassword());
	}
	
	@Test
	public void doNotReadPhotoStockWithoutName() {
		properties.remove("stock.test.name");
		configReader = new ConfigReader(properties);
		assertEquals("Must not read a stock when its name is not defined", 
				0, configReader.getPhotoStocks().size());
	}
	
}
