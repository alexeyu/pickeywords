package nl.alexeyu.photomate.thumbnail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class VideoBufferedImageProvider implements BufferedImageProvider {

    private static final int FRAME_NUMBER = 42;

    @Override
    public BufferedImage toBufferedImage(Path photoFile) {
        try {
            Picture picture = FrameGrab.getFrameFromFile(photoFile.toFile(), FRAME_NUMBER);
            return AWTUtil.toBufferedImage(picture);
        } catch (IOException | JCodecException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
