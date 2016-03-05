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

import nl.alexeyu.photomate.api.editable.EditablePhoto;
import nl.alexeyu.photomate.model.PhotoStock;

public class FtpUploadTask extends AbstractUploadTask implements CopyStreamListener {

    private static final int KEEP_ALIVE_TIMEOUT = 120;

    private static final Logger logger = LoggerFactory.getLogger("UploadTask");

    private final FTPClient client = new FTPClient();

    public FtpUploadTask(PhotoStock photoStock, EditablePhoto photo, int attemptsLeft) {
        super(photoStock, photo, attemptsLeft);
    }

    private void init() throws IOException {
        client.setCopyStreamListener(this);
        client.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT);
        client.connect(photoStock.ftpUrl());
        if (!client.login(photoStock.ftpUsername(), photoStock.ftpPassword())) {
            throw new IOException("Could not connect to " + photoStock);
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
        try (InputStream is = Files.newInputStream(photo.getPath())) {
            init();
            boolean stored = client.storeFile(photo.name(), is);
            boolean ack = client.completePendingCommand();
            if (stored && ack) {
                logger.info("%s\t has been uploaded to \t%s", photo.name(), photoStock.name());
                notifySuccess();
            } else {
                throw new IOException();
            }
        } catch (IOException ex) {
            logger.error("Could not upload %s to %s", photo.name(), photoStock.name(), ex);
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
