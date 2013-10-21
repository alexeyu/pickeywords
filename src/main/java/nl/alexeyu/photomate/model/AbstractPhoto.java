package nl.alexeyu.photomate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;

public abstract class AbstractPhoto implements Photo {
    
    private final AtomicReference<List<String>> keywords = new AtomicReference<>();
    
    private final AtomicReference<ImageIcon> thumbnail = new AtomicReference<>();
    
    @Override
    public ImageIcon getThumbnail() {
        return thumbnail.get();
    }

    @Override
    public List<String> getKeywords() {
        return keywords.get();
    }

    protected boolean hasKeywords() {
        return keywords.get() != null && !keywords.get().isEmpty();
    }
    
    protected boolean hasThumbnail() {
        return thumbnail.get() != null;
    }

    ResultProcessor<List<String>> getKeywordsResultProcessor() {
        return new ResultProcessor<List<String>>() {
            @Override
            public void process(List<String> keywords) {
                AbstractPhoto.this.keywords.set(new ArrayList<>(keywords));
            }
        };
    }
    
    ResultProcessor<ImageIcon> getThumbnailProcessor() {
        return new ResultProcessor<ImageIcon>() {
            @Override
            public void process(ImageIcon thumbnail) {
                AbstractPhoto.this.thumbnail.set(thumbnail);
            }
        };
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
