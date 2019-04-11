package net.ammensa.cron;

import net.ammensa.entity.Menu;
import net.ammensa.parse.MenuAntlrParser;
import net.ammensa.repository.MenuRepository;
import net.ammensa.scrape.MenuScraper;
import net.ammensa.utils.HttpDownload;
import net.ammensa.utils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class MenuUpdate {

    private static final Logger LOGGER = Logger.getLogger(MenuUpdate.class.getName());

    @Autowired
    private MenuScraper menuScraper;
    @Autowired
    private MenuAntlrParser menuParser;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private HttpDownload httpDownload;
    @Value("${server.port}")
    private int serverPort;

    @Scheduled(cron = "0 */10 10-12 * * 1-5", zone = "Europe/Rome")
    public void refreshMenu() {
        LOGGER.info("starting cron refresh");
        httpDownload.download("localhost:" + serverPort + "/update").subscribe((a) -> LOGGER.info("cron refresh complete"));
    }


    public Mono<Object> updateMenu() throws Exception {

        LOGGER.info("update");

        String menuUrl = menuScraper.scrapePdfMenuUrl();

        Mono<byte[]> monoMenuBytes = httpDownload.download(menuUrl);

        return monoMenuBytes.map(m -> {

            try {
                String menuString = PdfUtils.convertPdfToString(m);

                Menu menu = menuParser.parseMenu(menuString);

                menu.setUrl(menuUrl);
                menuRepository.save(menu);

                return Mono.empty();

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
