package nl.alexeyu.photomate.service.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.FtpEndpoint;

public class FtpUploadTask implements Runnable, CopyStreamListener {

    private static final int KEEP_ALIVE_TIMEOUT = 10;

    private static final Logger logger = LogManager.getLogger();

    private final FTPClient client = new FTPClient();
    
    private final EditablePhoto photo;
    
    private final FtpEndpoint endpoint;
    
    private final UploadNotifier notifier;

    public FtpUploadTask(EditablePhoto photo, FtpEndpoint endpoint, UploadNotifier notifier) {
    	this.photo = photo;
    	this.endpoint = endpoint;
    	this.notifier = notifier;
    }

    private void init() throws IOException {
        client.setCopyStreamListener(this);
        client.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
        client.connect(endpoint.url());
        if (!client.login(endpoint.username(), endpoint.password())) {
            throw new IOException("Could not connect to " + endpoint);
        }
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
    	notifier.notifyProgress(photo, endpoint, totalBytesTransferred);
    }

    @Override
    public void bytesTransferred(CopyStreamEvent e) {
    }

    @Override
    public void run() {
        try (var is = Files.newInputStream(photo.getPath())) {
            init();
            client.deleteFile(photo.name());
            boolean stored = client.storeFile(photo.name(), is);
            if (stored) {
                logger.info("Uploaded photo: {} to {}", photo, endpoint);
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
