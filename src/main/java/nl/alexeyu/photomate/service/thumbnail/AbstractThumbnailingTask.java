package nl.alexeyu.photomate.service.thumbnail;

import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alexeyu.photomate.model.LocalPhoto;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.service.TaskWeight;
import nl.alexeyu.photomate.service.UpdateListener;

public abstract class AbstractThumbnailingTask implements WeighedTask {
	
	private final Logger logger = LoggerFactory.getLogger("ThumbnailingTask");

	protected final LocalPhoto photo;
	
	protected final UpdateListener<LocalPhoto> observer;
	
	public AbstractThumbnailingTask(LocalPhoto photo, UpdateListener<LocalPhoto> observer) {
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
			logger.info("" + (System.currentTimeMillis() - time));
		} catch (Exception ex) {
			logger.error("Cannot make thumbnail", ex);
		}
	}

	@Override
	public TaskWeight getWeight() {
		return TaskWeight.HEAVY;
	}

	protected abstract Image scale() throws Exception;

}
