package net.ammensa.cron;

import net.ammensa.entity.Menu;
import net.ammensa.parse.MenuParser;
import net.ammensa.pdf.PdfConversion;
import net.ammensa.repository.MenuRepository;
import net.ammensa.scrape.MenuScraper;
import net.ammensa.utils.HttpDownload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

class MenuUpdateTest {

    private static final ZoneId ROME_ZONE_ID = ZoneId.of("Europe/Rome");

    @Test
    void updateMenuWhenDownloadOldMenu() throws Exception {

        final Instant menu_instant = Instant.parse("2020-04-10T20:20:20Z");
        final Clock menu_clock = Clock.fixed(menu_instant, ROME_ZONE_ID);
        final Menu yesterday_menu_stub = new Menu() {{
            setDate(LocalDate.now(menu_clock).minusDays(1));
        }};

        MenuUpdate menuUpdate = new MenuUpdate();

        ReflectionTestUtils.setField(menuUpdate, "italyClock", menu_clock);

        HttpDownload httpDownloadMock = Mockito.when(
                Mockito.mock(HttpDownload.class).download(Mockito.any())).thenReturn(Mono.just("".getBytes()))
                .getMock();
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        MenuParser menuParserMock = Mockito.when(
                Mockito.mock(MenuParser.class).parseMenu(Mockito.any())).thenReturn(yesterday_menu_stub)
                .getMock();
        ReflectionTestUtils.setField(menuUpdate, "menuParser", menuParserMock);

        MenuRepository menuRepositoryMock = Mockito.mock(MenuRepository.class);
        ReflectionTestUtils.setField(menuUpdate, "menuRepository", menuRepositoryMock);

        ReflectionTestUtils.setField(menuUpdate, "menuScraper", Mockito.mock(MenuScraper.class));
        ReflectionTestUtils.setField(menuUpdate, "pdfConversion", Mockito.mock(PdfConversion.class));

        menuUpdate.updateMenu().subscribe((a) ->
                Mockito.verify(menuRepositoryMock, Mockito.never()).save(Mockito.any()));
    }

    @Test
     void updateMenuWhenThereIsOldMenu() throws Exception {

        final Instant menu_instant = Instant.parse("2020-02-29T08:51:00Z");
        final Clock menu_clock = Clock.fixed(menu_instant, ROME_ZONE_ID);
        final Menu yesterday_menu_stub = new Menu() {{
            setDate(LocalDate.now(menu_clock).minusDays(1));
        }};
        final Menu today_menu_stub = new Menu() {{
            setDate(LocalDate.now(menu_clock));
        }};

        MenuUpdate menuUpdate = new MenuUpdate();

        ReflectionTestUtils.setField(menuUpdate, "italyClock", menu_clock);

        MenuRepository menuRepositoryMock = Mockito.when(
                Mockito.mock(MenuRepository.class).retrieve()).thenReturn(Optional.of(yesterday_menu_stub))
                .getMock();
        ReflectionTestUtils.setField(menuUpdate, "menuRepository", menuRepositoryMock);

        HttpDownload httpDownloadMock = Mockito.when(
                Mockito.mock(HttpDownload.class).download(Mockito.any())).thenReturn(Mono.just("".getBytes()))
                .getMock();
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);

        MenuParser menuParserMock = Mockito.when(
                Mockito.mock(MenuParser.class).parseMenu(Mockito.any())).thenReturn(today_menu_stub)
                .getMock();
        ReflectionTestUtils.setField(menuUpdate, "menuParser", menuParserMock);

        ReflectionTestUtils.setField(menuUpdate, "menuScraper", Mockito.mock(MenuScraper.class));
        ReflectionTestUtils.setField(menuUpdate, "pdfConversion", Mockito.mock(PdfConversion.class));

        menuUpdate.updateMenu().subscribe((a) -> {

            Mockito.verify(menuRepositoryMock, Mockito.atMostOnce()).delete();
            Mockito.verify(menuRepositoryMock).save(
                    Mockito.argThat(menu -> menu.getDate().equals(today_menu_stub.getDate()))
            );
        });

    }

    @Test
     void updateMenuWhenThereIsAlreadyTodayMenu() {

        final Instant menu_instant = Instant.parse("2019-02-20T10:00:00Z");
        final Clock menu_clock = Clock.fixed(menu_instant, ROME_ZONE_ID);
        final Menu today_menu_stub = new Menu() {{
            setDate(LocalDate.now(menu_clock));
        }};

        MenuUpdate menuUpdate = new MenuUpdate();

        ReflectionTestUtils.setField(menuUpdate, "italyClock", menu_clock);

        MenuRepository menuRepositoryMock = Mockito.when(
                Mockito.mock(MenuRepository.class).retrieve()).thenReturn(Optional.of(today_menu_stub))
                .getMock();
        ReflectionTestUtils.setField(menuUpdate, "menuRepository", menuRepositoryMock);

        menuUpdate.updateMenu();

        Mockito.verify(menuRepositoryMock, Mockito.never()).delete();
    }
}