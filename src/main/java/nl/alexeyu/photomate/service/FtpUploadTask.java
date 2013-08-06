package nl.alexeyu.photomate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoStock;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;

public class FtpUploadTask extends AbstractUploadTask implements CopyStreamListener {
	
	private final Logger logger = Logger.getLogger("FtpUploadTask");
	
	private final FTPClient client = new FTPClient();
	
	private int bufferSize = 1024 * 50;
	
	public FtpUploadTask(PhotoStock photoStock, Photo photo, int attemptsLeft, 
			UploadPhotoListener... uploadPhotoListeners) {
		super(photoStock, photo, attemptsLeft, uploadPhotoListeners);
		client.setCopyStreamListener(this);
		client.setControlKeepAliveTimeout(120);
	}

	public void init() throws IOException {
		client.connect(photoStock.getFtpUrl());
		client.login(photoStock.getFtpUsername(), photoStock.getFtpPassword());
	}
	
	public void destroy() {
		try {
			if (client.isConnected()) {
				client.logout();
				client.disconnect();
			}
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Could not disconnect", ex);
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
				Util.copyStream(is, os, bufferSize, fileSize, this, true);
			}
			checkFtpFile(fileSize);
			notifySuccess();
		} catch (Exception ex) {
			ex.printStackTrace();
			notifyError(ex);
		} finally {
			destroy();
		}
	}

	private void checkFtpFile(long expectedSize) throws IOException {
		FTPFile[] ftpFiles = client.listFiles(photo.getName());
		if (ftpFiles.length == 0) {
			throw new IllegalStateException("The file wasn't saved");
		}
		if (ftpFiles[0].getSize() != expectedSize) {
			throw new IllegalStateException("File size of " + photo + " on " 
					+ photoStock + " is " + ftpFiles[0].getSize() + ", " + expectedSize + " is expected");
		}
	}

}
