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

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

@Component
public class MenuUpdate {

    private static final Logger LOGGER = Logger.getLogger(MenuUpdate.class.getName());
    private static Clock italyClock = Clock.system(ZoneId.of("Europe/Rome"));

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
        updateMenu().subscribe(a -> LOGGER.info("cron refresh complete"));
    }

    public Mono<Menu> updateMenu() {

        LOGGER.info("update");

        return Mono.justOrEmpty(menuRepository.retrieve())
                .flatMap(menu -> {
                    if (!isTodayMenu(menu)) {
                        menuRepository.delete();
                        return Mono.empty();
                    } else
                        return Mono.just(menu);
                })
                .switchIfEmpty(Mono.defer(this::downloadAndSaveMenu));
    }

    private Mono<Menu> downloadAndSaveMenu() {
        try {
            String menuUrl = menuScraper.scrapePdfMenuUrl();
            return httpDownload.download(menuUrl)
                    .flatMap(m -> handleDownloadedMenu(menuUrl, m));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Mono<Menu> handleDownloadedMenu(String menuUrl, byte[] m) {
        try {
            String menuString = pdfConversion.toText(m);
            Menu menu = menuParser.parseMenu(menuString);

            if (isTodayMenu(menu)) {
                menu.setUrl(menuUrl);
                menuRepository.save(menu);
                return Mono.just(menu);
            } else {
                LOGGER.info("not today's menu. not saving.");
                return Mono.empty();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isTodayMenu(Menu menu) {

        ZonedDateTime today = ZonedDateTime.now(italyClock);

        LOGGER.fine(() -> String.format("the time right now is %s", today));

        return menu.getDate().isEqual(today.toLocalDate());
    }
}