package nl.alexeyu.photomate.model;

import java.util.Objects;

public class PhotoStock {
	
	private final String name;
	
	private final String iconUrl;
	
	private final FtpEndpoint ftpEndpoint;
	
	public PhotoStock(String name, String iconUrl, FtpEndpoint ftpEndpoint) {
		this.name = name;
		this.iconUrl = iconUrl;
		this.ftpEndpoint = ftpEndpoint;
	}

	public String name() {
		return name;
	}

	public String iconUrl() {
		return iconUrl;
	}

	public FtpEndpoint ftpEndpoint() {
		return ftpEndpoint;
	}

	@Override
	public String toString() {
		return "PhotoStock [" + name + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		if (!(obj instanceof PhotoStock)) 
			return false;
		PhotoStock other = (PhotoStock) obj;
		return Objects.equals(name, other.name);
	}

}
