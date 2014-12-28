package nl.alexeyu.photomate.service.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class FtpUploadTask extends AbstractUploadTask implements CopyStreamListener {
	
	private static final int KEEP_ALIVE_TIMEOUT = 120;

	private static final int BUFFER_SIZE = 1024 * 50;

	private static final Logger logger = LoggerFactory.getLogger("UploadTask");
	
	private final FTPClient client = new FTPClient();
	
	public FtpUploadTask(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft, 
			Collection<UploadPhotoListener> uploadPhotoListeners) {
		super(photoStock, photo, attemptsLeft, uploadPhotoListeners);
	}

	private void init() throws IOException {
        client.setCopyStreamListener(this);
        client.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
		client.connect(photoStock.ftpUrl());
		if (!client.login(photoStock.ftpUsername(), photoStock.ftpPassword())) {
			throw new IOException("Could not connect to " + photoStock);
		}
		client.setFileType(FTP.BINARY_FILE_TYPE);
	}
	
	@Override
	public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
		notifyProgress(totalBytesTransferred);
	}

	@Override
	public void bytesTransferred(CopyStreamEvent e) {}

	public void run() {
		notifyProgress(0);
		try (InputStream is = Files.newInputStream(photo.getPath())) {
			init();
			uploadFile(is);
			pause(1000);
			logger.info("%s\t%s\t%s", photo.name(), photo.fileSize(), photoStock.name());
			notifySuccess();
		} catch (IOException ex) {
			logger.error("", ex);
			notifyError(ex);
		} finally {
			destroy();
		}
	}

    private void uploadFile(InputStream is) throws IOException {
        try (OutputStream os = client.storeFileStream(photo.name())) {
            long fileSize = photo.fileSize();
            Preconditions.checkNotNull(os, "Could not create file on a remote server");
        	long uploadedBytes = Util.copyStream(is, os, BUFFER_SIZE, fileSize, this, true);
        	if (fileSize != uploadedBytes) {
        		logger.warn("%s, %s: expected %s, uploaded %s", photoStock, photo, fileSize, uploadedBytes);
        	}
        }
    }

	private void destroy() {
        try {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        } catch (IOException ex) {
            logger.error("Could not disconnect", ex);
        }
    }

}
