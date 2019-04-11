package net.ammensa;

import net.ammensa.cron.MenuUpdate;
import net.ammensa.entity.MenuStatus;
import net.ammensa.handler.MenuHandler;
import net.ammensa.parse.MenuAntlrParser;
import net.ammensa.repository.MenuRepository;
import net.ammensa.utils.HttpDownload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class ServeMenuIT {

    private static final ZoneId ROME_ZONE_ID = ZoneId.of("Europe/Rome");

    @Autowired
    private MenuUpdate menuUpdate;
    @Autowired
    private MenuAntlrParser menuAntlrParser;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Mock
    HttpDownload httpDownloadMock;

    private WebTestClient testClient;

    @BeforeEach
    private void init() {
        testClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void xmlMenu() throws Exception {

        /* TODO:
            - usare un webserver di test
            - evitare di fare lo scrape della pagina adisu
        */
        byte[] menuBytes = Files.readAllBytes(new ClassPathResource("menu/MERCOLEDI' 20 FEBBRAIO 2019 PRANZO.pdf").getFile().toPath());
        Mono<byte[]> monoMenuBytes = Mono.just(menuBytes);
        Mockito.when(httpDownloadMock.download(Mockito.any())).thenReturn(monoMenuBytes);
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-20T12:12:12").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            testClient
                    .get().uri("/")
                    .accept(MediaType.APPLICATION_XML)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().xpath("//menu/firstCourses").exists();
        });
    }

    @Test
    public void htmlMenu() throws Exception {

       /* TODO:
            - usare un webserver di test
            - evitare di fare lo scrape della pagina adisu
        */
        byte[] menuBytes = Files.readAllBytes(new ClassPathResource("menu/MERCOLEDI' 20 FEBBRAIO 2019 PRANZO.pdf").getFile().toPath());
        Mono<byte[]> monoMenuBytes = Mono.just(menuBytes);
        Mockito.when(httpDownloadMock.download(Mockito.any())).thenReturn(monoMenuBytes);
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-20T22:22:22").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            testClient
                    .get().uri("/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody().xpath("/html//div[@id='menu']/h2[1]").isEqualTo("Primi");
        });
    }

    @Test
    public void noMenuBeforeNoon() {

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-22T11:59:00").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.NOT_AVAILABLE.message);
    }

    @Test
    public void noMenuAfterNoon() {

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-22T13:00:00").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.STILL_NOT_AVAILABLE.message);
    }

    @Test
    public void saturdayMenuTest() {

        LocalDate saturday = Year.of(2000).atMonthDay(MonthDay.of(Month.SEPTEMBER, 1)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        Clock fixed = Clock.fixed(saturday.atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.message);
    }

    @Test
    public void sundayMenuTest() {

        LocalDate sunday = Year.of(2012).atMonthDay(MonthDay.of(Month.JULY, 1)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        System.out.println(sunday);

        Clock fixed = Clock.fixed(sunday.atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.message);
    }


    @Test
    public void christmasMenuTest() {

        Clock fixed = Clock.fixed(LocalDateTime.parse("2013-12-25T20:20:20").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.message);
    }

    @Test
    public void easterMenuTest() {

        Clock fixed = Clock.fixed(LocalDate.parse("1994-04-03").atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.message);
    }

    @Test
    public void liberationDayMenuTest() {

        Clock fixed = Clock.fixed(LocalDate.parse("2000-04-25").atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.message);
    }

    @Test
    public void sanMatteoMenuTest() {

        Clock fixed = Clock.fixed(LocalDate.parse("2018-09-21").atStartOfDay().atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        testClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.MENSA_CLOSED.message);
    }

    @Test
    public void oldMenuBeforeNoon() throws Exception {

        /* TODO:
            - usare un webserver di test
            - evitare di fare lo scrape della pagina adisu
        */
        byte[] menuBytes = Files.readAllBytes(new ClassPathResource("menu/MERCOLEDI' 20 FEBBRAIO 2019 PRANZO.pdf").getFile().toPath());
        Mono<byte[]> monoMenuBytes = Mono.just(menuBytes);
        Mockito.when(httpDownloadMock.download(Mockito.any())).thenReturn(monoMenuBytes);
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        Clock fixed = Clock.fixed(LocalDateTime.parse("2019-02-22T11:59:00").atZone(ROME_ZONE_ID).toInstant(), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            testClient
                    .get().uri("/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.NOT_AVAILABLE.message);

            assertTrue(menuRepository.retrieve().isEmpty());
        });
    }

    @Test
    public void oldMenuAfterNoon() throws Exception {

        /* TODO:
            - usare un webserver di test
            - evitare di fare lo scrape della pagina adisu
        */
        byte[] menuBytes = Files.readAllBytes(new ClassPathResource("menu/MERCOLEDI' 20 FEBBRAIO 2019 PRANZO.pdf").getFile().toPath());
        Mono<byte[]> monoMenuBytes = Mono.just(menuBytes);
        Mockito.when(httpDownloadMock.download(Mockito.any())).thenReturn(monoMenuBytes);
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        Clock fixed = Clock.fixed(Instant.parse("2019-02-22T13:00:00Z"), ROME_ZONE_ID);
        ReflectionTestUtils.setField(MenuHandler.class, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {
            testClient
                    .get().uri("/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .xpath("//div[@id='status']/h2[@id='message']").isEqualTo(MenuStatus.STILL_NOT_AVAILABLE.message);

            assertTrue(menuRepository.retrieve().isEmpty());
        });
    }
}

