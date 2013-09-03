package nl.alexeyu.photomate.service.thumbnail;

import static nl.alexeyu.photomate.ui.Constants.THUMBNAIL_SIZE;

import java.awt.Image;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.UpdateListener;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

public class Im4jThumbnailingTask extends AbstractThumbnailingTask {
	
	public Im4jThumbnailingTask(Photo photo, UpdateListener<Photo> observer) {
		super(photo, observer);
	}

	@Override
	protected Image scale() throws Exception {
		ConvertCmd cmd = new ConvertCmd();
		Stream2BufferedImage s2b = new Stream2BufferedImage();
		cmd.setOutputConsumer(s2b);
		IMOperation op = new IMOperation();
		op.addImage();
		op.resize(THUMBNAIL_SIZE.width, THUMBNAIL_SIZE.height);
		op.addImage("jpg:-");
		String fullName = photo.getFile().getAbsolutePath();
		cmd.run(op, fullName);
		return s2b.getImage();
	}

}
