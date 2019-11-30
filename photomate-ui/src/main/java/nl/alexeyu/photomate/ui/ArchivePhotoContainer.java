package nl.alexeyu.photomate.ui;

import static nl.alexeyu.photomate.ui.UiConstants.CLICKABLE_ICON_SIZE;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.inject.Inject;

import com.google.inject.name.Named;

import nl.alexeyu.photomate.api.LocalPhotoApi;
import nl.alexeyu.photomate.api.PhotoFileCleaner;
import nl.alexeyu.photomate.api.archive.ArchivePhoto;
import nl.alexeyu.photomate.thumbnail.FileThumbnailsProvider;
import nl.alexeyu.photomate.util.Configuration;
import nl.alexeyu.photomate.util.MediaFileProcessors;

public class ArchivePhotoContainer extends PhotoContainer<ArchivePhoto> {

    private static final int COLUMN_COUNT = 4;

    @Inject
    private Configuration configuration;

    @Inject
    @Named("archiveApi")
    private LocalPhotoApi<ArchivePhoto> photoApi;

    public ArchivePhotoContainer() {
        super(COLUMN_COUNT);
    }

    @Inject
    public void init() throws IOException {
        configuration.getProperty("archiveFolder").ifPresent(arcFolder -> {
            var dir = Paths.get(arcFolder);
            if (Files.exists(dir)) {
                readPhotos(dir);
            }
        });
    }

    private void readPhotos(Path dir) {
        var photos = photoApi.createPhotos(MediaFileProcessors.JPEG.apply(dir), ArchivePhoto::new);
        photoTable.setPhotos(photos);
        if (photos.size() > 0) {
            addMouseListener(new DeleteArchivedPhotoListener());
        }
    }

    private class DeleteArchivedPhotoListener extends MouseAdapter {

        private final Consumer<Path> cleaner = new PhotoFileCleaner("", FileThumbnailsProvider.CACHE_SUFFIX);

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = photoTable.rowAtPoint(e.getPoint());
            int col = photoTable.columnAtPoint(e.getPoint());
            if (getColumnRight(col) - e.getPoint().x < CLICKABLE_ICON_SIZE
                    && e.getPoint().y - getRowTop(row) < CLICKABLE_ICON_SIZE) {
                photoTable.getModel().getValueAt(row, col).ifPresent(archivePhoto -> {
                    archivePhoto.delete();
                    cleaner.accept(archivePhoto.getPath());
                    repaint();
                });
            }
        }

        private int getRowTop(int row) {
            return row * photoTable.getRowHeight();
        }

        private int getColumnRight(int col) {
            return IntStream.range(0, col + 1).map(index -> photoTable.getColumnModel().getColumn(index).getWidth()).sum();
        }

    }

}
