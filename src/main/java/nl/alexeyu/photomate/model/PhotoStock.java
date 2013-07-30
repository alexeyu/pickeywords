package nl.alexeyu.photomate.model;

public class PhotoStock {
	
	private final String name;
	
	private final String iconUrl;
	
	private final String ftpUrl;
	
	private final String ftpUsername;
	
	private final String ftpPassword;

	public PhotoStock(String name, String iconUrl, String ftpUrl,
			String ftpUsername, String ftpPassword) {
		this.name = name;
		this.iconUrl = iconUrl;
		this.ftpUrl = ftpUrl;
		this.ftpUsername = ftpUsername;
		this.ftpPassword = ftpPassword;
	}

	public String getName() {
		return name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	@Override
	public String toString() {
		return "PhotoStock [name=" + name + "]";
	}
	
}
