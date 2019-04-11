package net.ammensa;

import net.ammensa.cron.MenuUpdate;
import net.ammensa.entity.Menu;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ParseMenuIT {


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

        menuUpdate.updateMenu().subscribe((a) -> {

            Optional<Menu> retrievedMenu = menuRepository.retrieve();

            assertTrue(retrievedMenu.isPresent());
            assertFalse(retrievedMenu.get().getFirstCourses().isEmpty());
        });
    }
}

