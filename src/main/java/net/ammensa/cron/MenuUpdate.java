package net.ammensa.cron;

import net.ammensa.entity.Menu;
import net.ammensa.parse.MenuAntlrParser;
import net.ammensa.repository.MenuRepository;
import net.ammensa.scrape.MenuScraper;
import net.ammensa.utils.HttpDownload;
import net.ammensa.utils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MenuUpdate {

    private static final Logger LOGGER = Logger.getLogger(MenuUpdate.class.getName());

    private static final Pattern MENU_DATE_IN_URL_PATTERN = Pattern.compile("(\\d{1,2})[_]+(\\w+?)[_]+(\\d+)");

    @Autowired
    private MenuScraper menuScraper;
    @Autowired
    private MenuAntlrParser menuParser;
    @Autowired
    private MenuRepository menuRepository;

    //    @Scheduled(fixedRate = 500000)
    public void updateMenu() throws Exception {

        LOGGER.info("update");

        String menuUrl = menuScraper.scrapePdfMenuUrl();

        LOGGER.info(menuUrl);

        byte[] menuBytes = HttpDownload.download(menuUrl);
        String menuString = PdfUtils.convertPdfToString(menuBytes);

        Menu menu = menuParser.parseMenu(menuString);

        long scrapedMenuDateMillis = composeDateFromMenuUrl(menuUrl);

        LOGGER.log(Level.INFO, "{0}", scrapedMenuDateMillis);

        menu.setTimestamp(scrapedMenuDateMillis);
        menu.setUrl(menuUrl);

        menuRepository.save(menu);
    }

    private long composeDateFromMenuUrl(String menuUrl) throws ParseException {

        Matcher dateInUrlMatcher = MENU_DATE_IN_URL_PATTERN.matcher(menuUrl);
        dateInUrlMatcher.find();

        Calendar menuCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);

        Date month = new SimpleDateFormat("MMM", Locale.ITALY).parse(dateInUrlMatcher.group(2));

        /* setting menu month first due parsing of the month name */
        menuCalendar.setTime(month);
        menuCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateInUrlMatcher.group(1)));
        menuCalendar.set(Calendar.YEAR, Integer.parseInt(dateInUrlMatcher.group(3)));

        return menuCalendar.getTimeInMillis();
    }
}
