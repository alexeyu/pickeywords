package nl.alexeyu.photomate.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Predicate;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import nl.alexeyu.photomate.util.MediaFileProcessors;

public class DirChooser extends JPanel {

    public static final String DIR_PROPERTY = "dir";

    private final JFileChooser fileChooser = new JFileChooser();

    private final JLabel pathLabel = new JLabel("Select directory...");

    private final Path defaultFolder;

    public DirChooser(Path defaultFolder) {
        super(new BorderLayout());
        this.defaultFolder = defaultFolder;
        setBorder(UiConstants.EMPTY_BORDER);
        prepareFileChooser();
        preparePathLabel();
    }

    private void prepareFileChooser() {
        fileChooser.setFileFilter(new MediaFilesFilter());
        fileChooser.addActionListener(new SelectFileActionListener());
    }

    private void preparePathLabel() {
        this.add(pathLabel, BorderLayout.CENTER);
        pathLabel.setEnabled(false);
        pathLabel.addMouseListener(new PathSelector());
    }

    public void init() {
        selectDir(defaultFolder.toFile());
    }

    public void selectDir(File dir) {
        String path = dir.getAbsolutePath();
        pathLabel.setText(path);
        firePropertyChange(DIR_PROPERTY, null, path);
    }

    private final class PathSelector extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setCurrentDirectory(defaultFolder.toFile());
            fileChooser.showOpenDialog(getParent());
        }
    }

    private final class SelectFileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (JFileChooser.APPROVE_SELECTION.equals(event.getActionCommand())) {
                var dir = fileChooser.getSelectedFile();
                if (!dir.isDirectory()) {
                    dir = dir.getParentFile();
                }
                selectDir(dir);
            }
        }
    }

    private static class MediaFilesFilter extends FileFilter {
    	
    	private static final Predicate<Path> PREDICATE = MediaFileProcessors.JPEG.or(MediaFileProcessors.MPEG4);
    	
        @Override
        public boolean accept(File file) {
            return file.isDirectory() || PREDICATE.test(file.toPath());
        }

        @Override
        public String getDescription() {
            return "Photos and Videos";
        }
    }

}
