package nl.alexeyu.photomate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUploadTask extends AbstractUploadTask implements CopyStreamListener {
	
	private static final int KEEP_ALIVE_TIMEOUT = 120;

	private int BUFFER_SIZE = 1024 * 50;

	private final Logger logger = LoggerFactory.getLogger("UploadTask");
	
	private final FTPClient client = new FTPClient();
	
	public FtpUploadTask(PhotoStock photoStock, Photo photo, int attemptsLeft, 
			Collection<UploadPhotoListener> uploadPhotoListeners) {
		super(photoStock, photo, attemptsLeft, uploadPhotoListeners);
		client.setCopyStreamListener(this);
		client.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
	}

	public void init() throws IOException {
		client.connect(photoStock.getFtpUrl());
		if (!client.login(photoStock.getFtpUsername(), photoStock.getFtpPassword())) {
			throw new IllegalStateException("Could not connect to " + photoStock);
		}
	}
	
	public void destroy() {
		try {
			if (client.isConnected()) {
				client.logout();
				client.disconnect();
			}
		} catch (IOException ex) {
			logger.error("Could not disconnect", ex);
		}
	}
	
	@Override
	public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
		notifyProgress(totalBytesTransferred);
	}

	@Override
	public void bytesTransferred(CopyStreamEvent e) {}

	public void run() {
		notifyProgress(0);
		File file = photo.getFile();
		try (InputStream is = new FileInputStream(file)) {
			init();
			client.setFileType(FTP.BINARY_FILE_TYPE);
			long fileSize = file.length();
			try (OutputStream os = client.storeFileStream(photo.getName())) {
				if (os == null) {
					throw new IllegalStateException("Could not create file on a remote server");
				}
				long uploadedBytes = Util.copyStream(is, os, BUFFER_SIZE, fileSize, this, true);
				if (fileSize != uploadedBytes) {
					throw new IllegalStateException("Expected " + fileSize + ", uploaded " + uploadedBytes);
				}
			}
			Thread.sleep(500);
			logger.info(photo.getName() + "\t" + fileSize + "\t" + photoStock.getName());
			notifySuccess();
		} catch (Exception ex) {
			logger.error("", ex);
			notifyError(ex);
		} finally {
			destroy();
		}
	}

}
