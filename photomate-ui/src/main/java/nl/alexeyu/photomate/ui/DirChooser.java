package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Optional;

import javax.inject.Inject;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import nl.alexeyu.photomate.util.ConfigReader;
import nl.alexeyu.photomate.util.ImageUtils;

public class DirChooser extends JPanel {
	
	private JFileChooser fileChooser = new JFileChooser();
	
	private JLabel pathLabel = new JLabel("Select directory...");
	
	@Inject
	private ConfigReader configReader; 
	
	public DirChooser() {
		super(new BorderLayout());
		setBorder(UiConstants.EMPTY_BORDER);
		pathLabel.setEnabled(false);
		fileChooser.setFileFilter(new JpegFilter());
		fileChooser.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				if (JFileChooser.APPROVE_SELECTION.equals(event.getActionCommand())) {
					File dir = fileChooser.getSelectedFile();
					if (!dir.isDirectory()) {
						dir = dir.getParentFile();
					}
					selectDir(dir);
				}
			}
		});
		

		this.add(pathLabel, BorderLayout.CENTER);
		pathLabel.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setCurrentDirectory(new File(configReader.getProperty("defaultFolder", "")));
                fileChooser.showOpenDialog(getParent());
		    }
        });
	}

	public void init() {
        Optional<String> defaultDir = configReader.getProperty("defaultFolder");
        defaultDir.ifPresent(dir -> selectDir(new File(dir)));
	}
	
	public void selectDir(File dir) {
		String path = dir.getAbsolutePath();
		pathLabel.setText(path);
		firePropertyChange("dir", null, path);
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
