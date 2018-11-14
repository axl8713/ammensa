package net.ammensa.exception;

public class MenuParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public MenuParseException() {
		super("an error occurred while parsing the menu");
	}

	public MenuParseException(String message) {
		super(message);
	}
}
