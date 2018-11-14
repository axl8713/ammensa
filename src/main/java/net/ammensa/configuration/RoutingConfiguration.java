package net.ammensa.configuration;

import net.ammensa.flux.handler.MenuHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoutingConfiguration {

    @Bean
    public RouterFunction<ServerResponse> menuRouterFunction(MenuHandler menuHandler) {
        return RouterFunctions
                .route(GET("/menu").and(accept(MediaType.TEXT_HTML)), menuHandler::getHtmlMenu)
                .andRoute(GET("/menu").and(accept(MediaType.APPLICATION_XML, MediaType.TEXT_XML)), menuHandler::getXmlMenu)
                .andRoute(GET("/menu").and(accept(MediaType.ALL)), menuHandler::getJsonMenu)
                .andRoute(path("/update"), menuHandler::manualMenuUpdate);
    }
}
