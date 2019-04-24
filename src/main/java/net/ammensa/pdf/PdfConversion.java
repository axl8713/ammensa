package net.ammensa.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfConversion {

    public String toText(byte[] pdf) throws IOException {

        PdfReader pdfReader = new PdfReader(pdf);
        return PdfTextExtractor.getTextFromPage(pdfReader, 1, new SimpleTextExtractionStrategy());
    }
}
