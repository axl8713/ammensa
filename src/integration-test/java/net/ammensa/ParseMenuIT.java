package net.ammensa;

import net.ammensa.cron.MenuUpdate;
import net.ammensa.entity.Menu;
import net.ammensa.handler.MenuHandler;
import net.ammensa.repository.MenuRepository;
import net.ammensa.utils.HttpDownload;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ParseMenuIT {

    private static final ZoneId ROME_ZONE_ID = ZoneId.of("Europe/Rome");

    @Autowired
    private MenuUpdate menuUpdate;
    @Autowired
    private MenuRepository menuRepository;
    @Mock
    HttpDownload httpDownloadMock;

    @Test
    public void findAndParseMenu() throws Exception {

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
        ReflectionTestUtils.setField(menuUpdate, "ITALY_CLOCK", fixed);

        menuUpdate.updateMenu().subscribe((a) -> {

            Optional<Menu> retrievedMenu = menuRepository.retrieve();

            assertTrue(retrievedMenu.isPresent());
            assertFalse(retrievedMenu.get().getFirstCourses().isEmpty());
        });
    }
}