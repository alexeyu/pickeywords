package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.google.common.base.Strings;

import nl.alexeyu.photomate.util.ImageUtils;

public class DirChooser extends JPanel {

    public static final String DIR_PROPERTY = "dir";

    private final JFileChooser fileChooser = new JFileChooser();

    private final JLabel pathLabel = new JLabel("Select directory...");

    private final String defaultFolder;

    public DirChooser(String defaultFolder) {
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
    	if (defaultFolder != null) {
    		selectDir(new File(defaultFolder));
    	}
    }

    public void selectDir(File dir) {
        String path = dir.getAbsolutePath();
        pathLabel.setText(path);
        firePropertyChange(DIR_PROPERTY, null, path);
    }

    private final class PathSelector extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setCurrentDirectory(new File(Strings.nullToEmpty(defaultFolder)));
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
