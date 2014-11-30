package nl.alexeyu.photomate.api.editable;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoMetaData;

public class EditablePhoto extends LocalPhoto {
    
    public static final String PREVIEW_PROPERTY = "preview";

    private final AtomicReference<ImageIcon> preview = new AtomicReference<>();

    public EditablePhoto(Path path) {
        super(path);
    }

    public boolean isReadyToUpload() {
        if (getThumbnail() == null || getMetaData() == null) {
        	return false;
        }
        PhotoMetaData m = getMetaData();
		return !m.getDescription().isEmpty()
				&& !m.getCaption().isEmpty()
                && m.getKeywords() != null 
                && m.getKeywords().size() > 0
                && m.getKeywords().size() <= 50;
    }

    public ImageIcon getPreview() {
        return preview.get();
    }

    public void setPreview(ImageIcon preview) {
        firePropertyChanged(PREVIEW_PROPERTY, null, preview);
    }
    
	@Override
	public void addThumbnail(ImageIcon thumbnail) {
		if (getThumbnail() != null) {
			preview.set(thumbnail);
		} else {
			super.addThumbnail(thumbnail);
		}
	}

	@Override
	public int getThumbnailCount() {
		return 2;
	}

	@Override
	public ImageIcon getThumbnail(int index) {
		if (index == 1) {
			return getPreview();
		}
		return super.getThumbnail();
	}

    
}
