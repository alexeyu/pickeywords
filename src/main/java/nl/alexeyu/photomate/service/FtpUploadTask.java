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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.Util;

public class FtpUploadTask extends CopyStreamAdapter implements Runnable {
	
	private final Logger logger = Logger.getLogger("FtpUploadTask");
	
	private final FTPClient client = new FTPClient();
	
	private final PhotoStock photoStock;
	
	private final Photo photo;
	
	private final UploadPhotoListener uploadPhotoListener;
	
	private int bufferSize = 1024 * 50;
	
	public FtpUploadTask(PhotoStock photoStock, Photo photo, 
			UploadPhotoListener uploadPhotoListener) {
		this.photoStock = photoStock;
		this.photo = photo;
		this.uploadPhotoListener = uploadPhotoListener;
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
	public void bytesTransferred(long totalBytesTransferred,
			int bytesTransferred, long streamSize) {
		UploadPhotoEvent event = new UploadPhotoEvent(photo, photoStock, totalBytesTransferred);
		uploadPhotoListener.statusChanged(event);
	}

	public void run() {
		uploadPhotoListener.statusChanged(new UploadPhotoEvent(photo, photoStock, 0));
		
		if (StringUtils.isEmpty(photoStock.getFtpUrl())) {
			return;
		}

		File file = photo.getFile();
		try (InputStream is = new FileInputStream(file)) {
			init();
			client.setFileType(FTP.BINARY_FILE_TYPE);				
			try (OutputStream os = client.storeFileStream(file.getName())) {
				if (os == null) {
					throw new IllegalStateException("Could not create file on a remote server");
				}
				Util.copyStream(is, os, bufferSize, file.length(), this, true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			uploadPhotoListener.statusChanged(
					new UploadPhotoEvent(photo, photoStock, ex));
		} finally {
			destroy();
		}
	}
	
//	public static void main(String[] args) {
//		PhotoStock ps = new PhotoStock("Canstock", "", "ftp.canstockphoto.com", "Tetyana", "ilya1601max");
//		PhotoStock ps = new PhotoStock("Canstock", "", "ftp.shutterstock.com", "7colors@gmail.com", "tan1611lesha");
//		FtpUploader uploader = new FtpUploader(ps, 
//				new UploadPhotoListener() {
//					
//					@Override
//					public void statusChanged(UploadPhotoEvent event) {
//						System.out.println(">> " + event.getStatus() + "   " + event.getPercentUploaded());
//						if (event.getException() != null) {
//							event.getException().printStackTrace();
//						}
//					}
//				});
//		Photo photo = new Photo(new File("/home/lesha/_MG_0070.jpg"));
//		System.out.println("Uploading " + photo.getName() + " to " + ps.getName());
//		uploader.uploadPhoto(photo);
//	}
}
