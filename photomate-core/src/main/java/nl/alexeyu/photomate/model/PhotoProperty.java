package nl.alexeyu.photomate.model;

import java.util.stream.Stream;

public enum PhotoProperty {

    DESCRIPTION("description"),
    CAPTION("caption"),
    CREATOR("creator"),
    KEYWORDS("keywords");

    private final String propertyName;

    private PhotoProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String propertyName() {
        return propertyName;
    }

    public static boolean has(String s) {
        return Stream.of(values()).anyMatch(val -> val.propertyName.equals(s));
    }

    public static PhotoProperty of(String s) {
        return Stream.of(values())
                .filter(val -> val.propertyName.equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + s));
    }

}
