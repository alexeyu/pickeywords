package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.PrioritizedTask;
import nl.alexeyu.photomate.service.UpdateListener;

public abstract class AbstractThumbnailingTask implements PrioritizedTask {
	
	private final Logger logger = Logger.getLogger("photomate.ThumbnailingTask");

	protected final Photo photo;
	
	protected final UpdateListener<Photo> observer;
	
	public AbstractThumbnailingTask(Photo photo, UpdateListener<Photo> observer) {
		this.photo = photo;
		this.observer = observer;
	}

	@Override
	public void run() {
		try {
			long time = System.currentTimeMillis();
			Image image = scale();
			photo.setThumbnail(image);
			observer.onUpdate(photo);
			System.out.println(">> " + (System.currentTimeMillis() - time));
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot make thumbnail", ex);
		}
	}

	@Override
	public int getPriority() {
		return 20;
	}

	protected abstract Image scale() throws Exception;

}
