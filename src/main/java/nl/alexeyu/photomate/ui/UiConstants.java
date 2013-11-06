package nl.alexeyu.photomate.ui;

import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public interface UiConstants {

	int BORDER_WIDTH = 2;
	
	Border EMPTY_BORDER = new EmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH);
	
	Border LINE_BORDER = new LineBorder(Color.GRAY, BORDER_WIDTH );
	
}
