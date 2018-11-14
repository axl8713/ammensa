package net.ammensa.exception;

public class FileReadException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileReadException() {
		super("an error occurred while reading the file.");
	}

	public FileReadException(String message) {
		super(message);
	}
}
