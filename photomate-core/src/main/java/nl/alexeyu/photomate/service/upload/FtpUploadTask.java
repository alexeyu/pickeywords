package nl.alexeyu.photomate.service.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUploadTask implements Runnable, CopyStreamListener {

    private static final int KEEP_ALIVE_TIMEOUT = 10;

    private static final Logger logger = LoggerFactory.getLogger("UploadTask");

    private final FTPClient client = new FTPClient();
    
    private final UploadAttempt uploadAttempt;
    
    private final UploadNotifier notifier;

    public FtpUploadTask(UploadAttempt uploadAttempt, UploadNotifier notifier) {
    	this.uploadAttempt = uploadAttempt;
    	this.notifier = notifier;
    }

    private void init() throws IOException {
        client.setCopyStreamListener(this);
        client.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
        client.connect(uploadAttempt.getPhotoStock().ftpUrl());
        if (!client.login(uploadAttempt.getPhotoStock().ftpUsername(), uploadAttempt.getPhotoStock().ftpPassword())) {
            throw new IOException("Could not connect to " + uploadAttempt.getPhotoStock());
        }
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
    	notifier.notifyProgress(uploadAttempt, totalBytesTransferred);
    }

    @Override
    public void bytesTransferred(CopyStreamEvent e) {
    }

    @Override
    public void run() {
        try (InputStream is = Files.newInputStream(uploadAttempt.getPhoto().getPath())) {
            init();
            client.deleteFile(uploadAttempt.getPhoto().name());
            boolean stored = client.storeFile(uploadAttempt.getPhoto().name(), is);
            if (stored) {
                logger.info("Uploaded successful: %s", uploadAttempt);
            } else {
                throw new UploadException();
            }
        } catch (IOException ex) {
        	throw new UploadException(ex);
        } finally {
            destroy();
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
