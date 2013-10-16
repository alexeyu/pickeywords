package nl.alexeyu.photomate.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.service.UpdateListener;

public class KeywordPicker {
	
	private JPanel panel;
	
	private JTextField keywordText;
	
	private JList<String> actualKeywordList  = new JList<>();
	
	private JButton addKeywordButton;
	
	private JButton removeKeywordButton;
	
	private UpdateListener<String> addKeywordListener;
	
	private UpdateListener<String> removeKeywordListener;
	
	public KeywordPicker() {
		build();
	}
	
	private void build() {
		panel = new JPanel(new BorderLayout(5, 5));

		JPanel textPane = new JPanel();
		textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));
		JLabel label = new JLabel("Keyword: ");
		textPane.add(label);
		textPane.add(Box.createRigidArea(new Dimension(5,0)));
		keywordText = new JTextField();
		textPane.add(keywordText);
		textPane.add(Box.createRigidArea(new Dimension(5,0)));
		addKeywordButton = new JButton("+");
		addKeywordButton.addActionListener(new AddKeywordTask());
		textPane.add(addKeywordButton);
		textPane.add(Box.createRigidArea(new Dimension(5,0)));
		removeKeywordButton = new JButton("-"); 
		removeKeywordButton.addActionListener(new RemoveKeywordTask());
		textPane.add(removeKeywordButton);
		
		panel.add(textPane, BorderLayout.NORTH);
		panel.add(new JScrollPane(actualKeywordList), BorderLayout.CENTER);
	}
	
	public void setPhoto(Photo photo) {
		ListModel<String> listModel = new KeywordListModel(photo);
		actualKeywordList.setModel(listModel);
		actualKeywordList.revalidate();
		actualKeywordList.repaint();
	}
	
	public JComponent getComponent() {
		return panel;
	}

	public void onKeywordAdd(String keyword) {
		if (addKeywordListener != null) {
			addKeywordListener.onUpdate(keyword);
		}
	}
	
	public void onKeywordRemove(String keyword) {
		if (removeKeywordListener != null) {
			removeKeywordListener.onUpdate(keyword);
		}
	}

	public void setAddKeywordListener(UpdateListener<String> addKeywordListener) {
		this.addKeywordListener = addKeywordListener;
	}

	public void setRemoveKeywordListener(
			UpdateListener<String> removeKeywordListener) {
		this.removeKeywordListener = removeKeywordListener;
	}

	private class AddKeywordTask implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String keyword = keywordText.getText();
			if (!keyword.trim().isEmpty()) {
				ListModel<String> model = actualKeywordList.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					if (keyword.equalsIgnoreCase(model.getElementAt(i).toString())) {
						return;
					}
				}
				onKeywordAdd(keyword);
			}
		}
		
	}
	
	private class RemoveKeywordTask implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String keyword = (String) actualKeywordList.getSelectedValue();
			if (keyword != null) {
				onKeywordRemove(keyword);
			}
		}
		
	}

}
