package nl.alexeyu.photomate.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public interface Constants {

	int BORDER_WIDTH = 4;
	
	Border EMPTY_BORDER = new EmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH);
	
	Border LINE_BORDER = new LineBorder(Color.GRAY, BORDER_WIDTH );
	
	Dimension THUMBNAIL_SIZE = new Dimension(250, 200);
	
}
