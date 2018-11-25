package net.ammensa.flux.handler;

import net.ammensa.cron.MenuUpdate;
import net.ammensa.entity.Menu;
import net.ammensa.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;

import org.springframework.http.MediaType;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.web.reactive.result.view.Rendering;

@Component
public class MenuHandler {

    @Autowired
    private MenuUpdate menuUpdate;
    @Autowired
    private MenuRepository menuRepository;

    public Mono<ServerResponse> getJsonMenu(ServerRequest request) {
        try {
            return ServerResponse.ok().body(fromObject(menuRepository.retrieve()));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }

    public Mono<ServerResponse> getXmlMenu(ServerRequest request) {
        try {
            return
//                    Rendering.view("menu_xml")
//                    .modelAttribute("menu", menuRepository.retrieve());

                    ServerResponse.ok()
                            .contentType(MediaType.TEXT_XML)
                            .render("menu_xml", new HashMap<String, Menu>() {
                                {
                                    put("menu", menuRepository.retrieve());
                                }
                            });
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }

    public Mono<ServerResponse> getHtmlMenu(ServerRequest request) {
        try {
            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .render("menu", new HashMap<String, Menu>() {
                        {
                            put("menu", menuRepository.retrieve());
                        }
                    });
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }

    public Mono<ServerResponse> manualMenuUpdate(ServerRequest request) {
        try {

            menuRepository.delete();

            menuUpdate.updateMenu();
            return ServerResponse.ok().body(fromObject(menuRepository.retrieve()));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }

}
