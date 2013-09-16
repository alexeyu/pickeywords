package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import nl.alexeyu.photomate.service.UpdateListener;
import nl.alexeyu.photomate.util.ImageUtils;

public class PhotoChooser extends JPanel {
	
	public PhotoChooser(final Container parent, final UpdateListener<File> fileListener, final String defaultFolder) {
		super(new BorderLayout());
		final JTextField textField = new JTextField("Select directory...");
		textField.setEnabled(false);

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new JpegFilter());
		fileChooser.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				if (JFileChooser.APPROVE_SELECTION.equals(event.getActionCommand())) {
					File dir = fileChooser.getSelectedFile();
					if (!dir.isDirectory()) {
						dir = dir.getParentFile();
					}
					textField.setText(dir.getAbsolutePath());
					fileListener.onUpdate(dir);
				}
			}
		});
		
		JButton chooseButton = new JButton("...");
		chooseButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.setCurrentDirectory(new File(defaultFolder));
				fileChooser.showOpenDialog(parent);
			}
		});

		this.add(textField, BorderLayout.CENTER);
		this.add(chooseButton, BorderLayout.EAST);
	}

	private static class JpegFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.isDirectory() || ImageUtils.isJpeg(file);
		}

		@Override
		public String getDescription() {
			return "Photos";
		}
	}

}
