package net.ammensa.scrape;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MenuScraper {

    private static final Logger LOGGER = Logger.getLogger(MenuScraper.class.getName());

    @Value("${ammensa.menuPageUrl}")
    private String menuPageUrl;

    public String scrapePdfMenuUrl() throws Exception {
        try {

            String scrapedMenuUrl = scrapeMenuPdfRelativeUrlJsoup();

            LOGGER.log(Level.INFO, () -> "scraped menu's URL: " + scrapedMenuUrl);

            return scrapedMenuUrl;

        } catch (Exception ex) {
            throw new Exception("an error occurred while scraping the menu (" + ex.getMessage() + ")", ex);
        }
    }

    private String scrapeMenuPdfRelativeUrlJsoup() throws Exception {
        Response response = Jsoup.connect(menuPageUrl).timeout(0).execute();
        Document document = response.parse();

        Elements aMenuPranzo = document.getElementsMatchingOwnText("Pranzo");

        if (aMenuPranzo.isEmpty()) {
            throw new Exception("no pdf in page");
        }

        return aMenuPranzo.get(0).attr("href");
    }
}