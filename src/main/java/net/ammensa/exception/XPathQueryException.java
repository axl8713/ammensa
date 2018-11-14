package net.ammensa.exception;

public class XPathQueryException extends Exception {
	private static final long serialVersionUID = 1L;

	public XPathQueryException() {
		super("an error occurred while evaluating the XPath query");
	}

	public XPathQueryException(String message) {
		super(message);
	}
}
