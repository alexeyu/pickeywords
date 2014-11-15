package nl.alexeyu.photomate.api.editable;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.LocalPhoto;
import nl.alexeyu.photomate.model.PhotoMetaData;

public class EditablePhoto extends LocalPhoto {
    
    public static final String PREVIEW_PROPERTY = "preview";

    private final AtomicReference<ImageIcon> preview = new AtomicReference<>();

    public EditablePhoto(Path file) {
        super(file);
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
        if (!this.preview.compareAndSet(null, preview)) {
            throw new IllegalStateException("Attempt to set preview 2nd time");
        }
        firePropertyChanged(PREVIEW_PROPERTY, null, preview);
    }

	@Override
	public boolean hasPreview() {
		return true;
	}

}
