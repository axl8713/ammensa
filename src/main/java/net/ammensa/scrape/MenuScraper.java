package net.ammensa.scrape;

import net.ammensa.property.AMMensaProperties;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MenuScraper {

    private static final Logger LOGGER = Logger.getLogger(MenuScraper.class.getName());

    private static final Pattern MENU_URL_PATTERN = Pattern.compile("fileadmin/user_upload/menu[a-zA-Z0-9/_]+\\.pdf");

    public String scrapePdfMenuUrl() throws Exception {

        try {

            String scrapedMenuUrl = AMMensaProperties.retrieveProperty("adisuHostUrl") + "/" + scrapeMenuPdfRelativeUrlJsoup();

            LOGGER.info("scraped menu's URL: " + scrapedMenuUrl);

            return scrapedMenuUrl;

        } catch (Exception ex) {
            throw new Exception("an error occourred " + "while scraping the menu (" + ex.getMessage() + ")", ex);
        }

    }

    private String scrapeMenuPdfRelativeUrlJsoup() throws Exception {
        Response response = Jsoup.connect(AMMensaProperties.retrieveProperty("menuPageUrl")).timeout(0).execute();
        Document document = response.parse();

        Element aMenuPranzo = document.getElementsByAttributeValue("title", "[application/pdf]").get(0);

        return aMenuPranzo.attr("href");
    }

    public static void findUrl(byte[] menuPageHtmlBytes) {
        Matcher m = MENU_URL_PATTERN
                .matcher(new String(menuPageHtmlBytes));

        if (m.find())
            System.out.println("+++++++++++++++++>> FOUND: " + m.group());
    }
}