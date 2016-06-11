package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import nl.alexeyu.photomate.util.ImageUtils;

public class DirChooser extends JPanel {

    public static final String DIR_PROPERTY = "dir";

    private final JFileChooser fileChooser = new JFileChooser();

    private final JLabel pathLabel = new JLabel("Select directory...");

    private final Optional<String> defaultFolder;

    public DirChooser(Optional<String> defaultFolder) {
        super(new BorderLayout());
        this.defaultFolder = defaultFolder;
        setBorder(UiConstants.EMPTY_BORDER);
        prepareFileChooser();
        preparePathLabel();
    }

    private void prepareFileChooser() {
        fileChooser.setFileFilter(new JpegFilter());
        fileChooser.addActionListener(new SelectFileActionListener());
    }

    private void preparePathLabel() {
        this.add(pathLabel, BorderLayout.CENTER);
        pathLabel.setEnabled(false);
        pathLabel.addMouseListener(new PathSelector());
    }

    public void init() {
        defaultFolder.ifPresent(dir -> selectDir(new File(dir)));
    }

    public void selectDir(File dir) {
        String path = dir.getAbsolutePath();
        pathLabel.setText(path);
        firePropertyChange(DIR_PROPERTY, null, path);
    }

    private final class PathSelector extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setCurrentDirectory(new File(defaultFolder.orElse("")));
            fileChooser.showOpenDialog(getParent());
        }
    }

    private final class SelectFileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (JFileChooser.APPROVE_SELECTION.equals(event.getActionCommand())) {
                File dir = fileChooser.getSelectedFile();
                if (!dir.isDirectory()) {
                    dir = dir.getParentFile();
                }
                selectDir(dir);
            }
        }
    }

    private static class JpegFilter extends FileFilter {
        @Override
        public boolean accept(File file) {
            return file.isDirectory() || ImageUtils.isJpeg(file.toPath());
        }

        @Override
        public String getDescription() {
            return "Photos";
        }
    }

}
