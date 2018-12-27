package nl.alexeyu.photomate.util;

import javax.swing.ImageIcon;

public class StaticImageProvider {

    public static ImageIcon getImage(String name) {
        var url = StaticImageProvider.class.getResource("/img/" + name);
        return new ImageIcon(url);
    }

}
