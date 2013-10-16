package nl.alexeyu.photomate.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.PhotoApi;

public abstract class AbstractPhoto implements Photo {
    
    private AtomicReference<List<String>> keywords;
    
    private AtomicReference<ImageIcon> thumbnail;
    
    private final PhotoApi photoApi;
    
    public AbstractPhoto(PhotoApi photoApi) {
        this.photoApi = photoApi;
    }

    @Override
    public ImageIcon getThumbnail() {
        if (thumbnail == null) {
            thumbnail = new AtomicReference<>();
            photoApi.provideThumbnail(getThumbnailUrl(), new ResultFiller<ImageIcon>() {
                @Override
                public void fill(ImageIcon thumbnail) {
                    AbstractPhoto.this.thumbnail.set(thumbnail);
                }
            });
        }
        return thumbnail.get();
    }

    @Override
    public List<String> getKeywords() {
        if (keywords == null) {
            keywords = new AtomicReference<>();
            photoApi.provideKeywords(getUrl(), new ResultFiller<List<String>>() {
                @Override
                public void fill(List<String> keywords) {
                    AbstractPhoto.this.keywords.set(keywords);
                }
            });
        }
        return keywords.get();
    }

    protected abstract String getThumbnailUrl();
    
    protected abstract String getUrl();
    
    protected boolean hasKeywords() {
        return keywords != null && keywords.get() != null && !keywords.get().isEmpty();
    }
    
    protected boolean hasThumbnail() {
        return thumbnail != null && thumbnail.get() != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Photo other = (Photo) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

}
