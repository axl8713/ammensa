package net.ammensa.cron;

import net.ammensa.entity.Menu;
import net.ammensa.parse.MenuParser;
import net.ammensa.pdf.PdfConversion;
import net.ammensa.repository.MenuRepository;
import net.ammensa.scrape.MenuScraper;
import net.ammensa.utils.HttpDownload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.*;
import java.util.logging.Logger;

@Component
public class MenuUpdate {

    private static final Logger LOGGER = Logger.getLogger(MenuUpdate.class.getName());
    private static Clock ITALY_CLOCK = Clock.system(ZoneId.of("Europe/Rome"));


    @Autowired
    private MenuScraper menuScraper;
    @Autowired
    private MenuParser menuParser;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private HttpDownload httpDownload;
    @Autowired
    private PdfConversion pdfConversion;

    @Scheduled(cron = "0 */10 10-12 * * 1-5", zone = "Europe/Rome")
    public void refreshMenu() {
        LOGGER.info("starting cron refresh");
        updateMenu().subscribe((a) -> LOGGER.info("cron refresh complete"));
    }


    public Mono<Object> updateMenu() {
        try {

            LOGGER.info("update");

            menuRepository.delete();

            String menuUrl = menuScraper.scrapePdfMenuUrl();

            Mono<byte[]> monoMenuBytes = httpDownload.download(menuUrl);

            return monoMenuBytes.map(m -> {
                try {
                    String menuString = pdfConversion.toText(m);
                    Menu menu = menuParser.parseMenu(menuString);

                    if (isTodayMenu(menu)) {
                        menu.setUrl(menuUrl);
                        menuRepository.save(menu);
                    } else {
                        LOGGER.info("not today's menu. not saving.");
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                return Mono.empty();
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isTodayMenu(Menu menu) {
        LocalDate menuDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(menu.getTimestamp()), ITALY_CLOCK.getZone()).toLocalDate();
        LocalDate now = ZonedDateTime.now(ITALY_CLOCK).toLocalDate();
        return menuDate.isEqual(now);
    }
}
