package jp.co.yuu.chintaiDB.domain;

public class ScrapingStatus {

	private String message = null;
	private boolean isScraping = false;

	public ScrapingStatus() {
	}

	public ScrapingStatus(String message, boolean isScraping) {
		this.message = message;
		this.isScraping = isScraping;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isScraping() {
		return isScraping;
	}
	public void setScraping(boolean isScraping) {
		this.isScraping = isScraping;
	}

}
