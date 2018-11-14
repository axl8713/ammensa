package net.ammensa.exception;

public class HttpDownloadException extends Exception {

	private static final long serialVersionUID = 1L;

	public HttpDownloadException() {
		super("an error occurred while downloading the resource");
	}

	public HttpDownloadException(String message) {
		super(message);
	}
}