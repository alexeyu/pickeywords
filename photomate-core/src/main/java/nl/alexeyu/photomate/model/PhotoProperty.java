package nl.alexeyu.photomate.model;

public enum PhotoProperty {

	DESCRIPTION("description"),
	CAPTION("caption"),
	CREATOR("creator"),
	KEYWORDS("keywords");
	
	private final String propertyName;

	private PhotoProperty(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}
	
	public static boolean has(String s) {
		for (PhotoProperty pp : values()) {
			if (pp.propertyName.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public static PhotoProperty of(String s) {
		for (PhotoProperty pp : values()) {
			if (pp.propertyName.equals(s)) {
				return pp;
			}
		}
		throw new IllegalArgumentException("Not found: " + s);
	}

}
