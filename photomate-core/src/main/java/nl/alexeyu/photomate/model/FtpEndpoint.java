package nl.alexeyu.photomate.model;

public final class FtpEndpoint {
	
	private final String url;
	
	private final String username;
	
	private final String password;

	public FtpEndpoint(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String url() {
		return url;
	}

	public String username() {
		return username;
	}

	public String password() {
		return password;
	}

	@Override
	public String toString() {
		return url;
	}
	
}
