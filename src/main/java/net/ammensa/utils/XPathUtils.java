package net.ammensa.utils;

import net.ammensa.exception.XPathQueryException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public abstract class XPathUtils {

	public static String evaluateQuery(InputStream inputStream, String query)
			throws XPathQueryException {

		String result = "";

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			disableXmlValidations(factory);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inputStream);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(query);
			result = expr.evaluate(doc);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new XPathQueryException("cannot evaluate XPath query: " + ex.getMessage());
		}
		return result;
	}

	private static void disableXmlValidations(DocumentBuilderFactory factory)
			throws ParserConfigurationException {
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		factory.setFeature("http://xml.org/sax/features/namespaces", false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	}
}