package net.ammensa.utils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.IOException;

public abstract class PdfUtils {
	
	public static String convertPdfToString(byte[] pdf) throws IOException {

		PdfReader pdfReader = new PdfReader(pdf);
		return PdfTextExtractor.getTextFromPage(pdfReader, 1, new SimpleTextExtractionStrategy());
	}
}
