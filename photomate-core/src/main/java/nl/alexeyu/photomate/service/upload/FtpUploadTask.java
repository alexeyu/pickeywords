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

public class FtpUploadTask extends AbstractUploadTask implements CopyStreamListener {

    private static final int KEEP_ALIVE_TIMEOUT = 120;

    private static final Logger logger = LoggerFactory.getLogger("UploadTask");

    private final FTPClient client = new FTPClient();

    public FtpUploadTask(PhotoToStock photoToStock, int attemptsLeft) {
        super(photoToStock, attemptsLeft);
    }

    private void init() throws IOException {
        client.setCopyStreamListener(this);
        client.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
        client.connect(photoToStock.getPhotoStock().ftpUrl());
        if (!client.login(photoToStock.getPhotoStock().ftpUsername(), photoToStock.getPhotoStock().ftpPassword())) {
            throw new IOException("Could not connect to " + photoToStock.getPhotoStock());
        }
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
        notifyProgress(totalBytesTransferred);
    }

    @Override
    public void bytesTransferred(CopyStreamEvent e) {
    }

    @Override
    public void run() {
        notifyProgress(0);
        try (InputStream is = Files.newInputStream(photoToStock.getPhoto().getPath())) {
            init();
            boolean stored = client.storeFile(photoToStock.getPhoto().name(), is);
            boolean ack = client.completePendingCommand();
            if (stored && ack) {
                logger.info("Uploaded successful: %s", photoToStock);
                notifySuccess();
            } else {
                throw new IOException();
            }
        } catch (IOException ex) {
            logger.error("Could not upload %s", photoToStock, ex);
            notifyError(ex);
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
