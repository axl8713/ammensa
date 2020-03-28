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

import java.time.LocalDateTime;

class MenuUpdateTest {

    @Test
    void updateMenu() throws Exception {

        Mono<byte[]> monoMenuBytesStub = Mono.just("".getBytes());
        HttpDownload httpDownloadMock = Mockito.when(Mockito.mock(HttpDownload.class).download(Mockito.any())).thenReturn(monoMenuBytesStub).getMock();

        MenuRepository menuRepositoryMock = Mockito.mock(MenuRepository.class);

        Menu menuStub = new Menu() {{
            setDate(LocalDateTime.now().minusDays(1).toLocalDate());
        }};
        MenuParser menuParserMock = Mockito.when(Mockito.mock(MenuParser.class).parseMenu(Mockito.any())).thenReturn(menuStub).getMock();

        MenuUpdate menuUpdate = new MenuUpdate();

        ReflectionTestUtils.setField(menuUpdate, "menuScraper", Mockito.mock(MenuScraper.class));
        ReflectionTestUtils.setField(menuUpdate, "menuRepository", menuRepositoryMock);
        ReflectionTestUtils.setField(menuUpdate, "httpDownload", httpDownloadMock);
        ReflectionTestUtils.setField(menuUpdate, "pdfConversion", Mockito.mock(PdfConversion.class));
        ReflectionTestUtils.setField(menuUpdate, "menuParser", menuParserMock);

        menuUpdate.updateMenu().subscribe((a) -> {
            Mockito.verify(menuRepositoryMock, Mockito.never()).save(Mockito.any());
        });
    }
}