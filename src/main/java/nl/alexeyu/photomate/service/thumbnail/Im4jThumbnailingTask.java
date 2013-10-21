package nl.alexeyu.photomate.service.thumbnail;

import static nl.alexeyu.photomate.ui.Constants.THUMBNAIL_SIZE;

import java.awt.Image;
import java.io.File;

import nl.alexeyu.photomate.service.ThumbnailProvider;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

public class Im4jThumbnailingTask implements ThumbnailProvider {

    @Override
    public Image getThumbnail(File photoFile) throws Exception {
        ConvertCmd cmd = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        cmd.setOutputConsumer(s2b);
        IMOperation op = new IMOperation();
        op.addImage();
        op.resize(THUMBNAIL_SIZE.width, THUMBNAIL_SIZE.height);
        op.addImage("jpg:-");
        cmd.run(op, photoFile.getAbsolutePath());
        return s2b.getImage();
	}

}
