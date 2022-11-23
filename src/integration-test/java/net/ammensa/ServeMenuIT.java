package net.ammensa;

import net.ammensa.cron.ApplicationStartMenuUpdater;
import net.ammensa.cron.MenuUpdate;
import net.ammensa.entity.MenuStatus;
import net.ammensa.handler.MenuHandler;
import net.ammensa.repository.MenuRepository;
import net.ammensa.scrape.MenuScraper;
import net.ammensa.utils.HttpDownload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.time.*;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@MockBean(ApplicationStartMenuUpdater.class)
class ServeMenuIT {

    private static final ZoneId ROME_ZONE_ID = ZoneId.of("Europe/Rome");

    @Autowired
    private MenuUpdate menuUpdate;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Mock
    HttpDownload httpDownloadMock;
    @Mock
    MenuScraper menuScraperMock;

    private WebTestClient webTestClient;

    @BeforeEach
    private void initWebTestClient() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .responseTimeout(Duration.ofSeconds(20))
                .build();
    }

    @BeforeEach
    public void stubMenuUpdateDownloadAndScrape() throws Exception {
        byte[] menuBytes = Files.readAllBytes(new ClassPathResource("menu/MERCOLEDI' 20 FEBBRAIO 2019 PRANZO.pdf").getFile().toPath());
        Mono<byte[]> monoMenuBytes = Mono.just(menuBytes);
        Mockito.when(httpDownloadMock.download(Mockito.any())).thenReturn(monoMenuBytes);
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        Mockito.when(menuScraperMock.scrapePdfMenuUrl()).thenReturn("pdfmenuurl");
        ReflectionTestUtils.setField(menuUpdate, "menuScraper", menuScraperMock);
    }

    @Test
    void xmlMenu() {

        /* TODO:
            - usare un webserver di test
        */
        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-20T12:12:12").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);
        ReflectionTestUtils.setField(menuUpdate, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe(a -> {
            webTestClient
                    .get().uri("/")
                    .accept(MediaType.APPLICATION_XML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().xpath("//menu/firstCourses").exists();
        });
    }

    @Test
    void htmlMenu() {

       /* TODO:
            - usare un webserver di test
        */
        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-20T22:22:22").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(menuUpdate, "ITALY_CLOCK", fixed);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            webTestClient
                    .get().uri("/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().xpath("/html//div[@id='menu']/h2[1]").isEqualTo("Primi");
        });
    }

    @Test
    void oldMenuBeforeNoon() {

        /* TODO:
            - usare un webserver di test
        */
        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-22T11:59:00").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            webTestClient
                    .get().uri("/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.NOT_AVAILABLE.getMessage());

            assertTrue(menuRepository.retrieve().isEmpty());
        });
    }

    @Test
    void oldMenuAfterNoon() {

        /* TODO:
            - usare un webserver di test
        */
        Clock fixed = Clock.fixed(Instant.parse("2019-02-22T13:00:00Z"), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            webTestClient
                    .get().uri("/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.STILL_NOT_AVAILABLE.getMessage());

            assertTrue(menuRepository.retrieve().isEmpty());
        });
    }

    @Test
    void noMenuBeforeNoon() {

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-22T11:59:00").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.NOT_AVAILABLE.getMessage());
    }

    @Test
    void noMenuAfterNoon() {

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-22T13:00:00").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.STILL_NOT_AVAILABLE.getMessage());
    }

    @Test
    void saturdayMenuTest() {

        LocalDate saturday = Year.of(2000).atMonthDay(MonthDay.of(Month.SEPTEMBER, 1)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        Clock fixed = Clock.fixed(saturday.atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.getMessage());
    }

    @Test
    void sundayMenuTest() {

        LocalDate sunday = Year.of(2012).atMonthDay(MonthDay.of(Month.JULY, 1)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Clock fixed = Clock.fixed(sunday.atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.getMessage());
    }


    @Test
    void christmasMenuTest() {

        Clock fixed = Clock.fixed(LocalDateTime.parse("2013-12-25T20:20:20").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.getMessage());
    }

    @Test
    void easterMenuTest() {

        Clock fixed = Clock.fixed(LocalDate.parse("1994-04-03").atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.getMessage());
    }

    @Test
    void liberationDayMenuTest() {

        Clock fixed = Clock.fixed(LocalDate.parse("2000-04-25").atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.getMessage());
    }

    @Test
    void sanMatteoMenuTest() {

        Clock fixed = Clock.fixed(LocalDate.parse("2018-09-21").atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.getMessage());
    }
}