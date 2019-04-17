package net.ammensa.configuration;

import net.ammensa.handler.MenuHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class RoutingConfiguration {

    @Bean
    public RouterFunction<ServerResponse> menuRouterFunction(MenuHandler menuHandler) {
        return RouterFunctions
                .route(GET("/"), menuHandler::serveMenu)
                .andRoute(path("/update"), menuHandler::manualMenuUpdate);
    }
}
