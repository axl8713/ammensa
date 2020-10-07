package net.ammensa.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class HttpDownload {

    private static final Logger LOGGER = Logger.getLogger(HttpDownload.class.getName());

    private final WebClient web_client = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    /* 10 MB */
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10))
                    .build())
            .build();

    public Mono<byte[]> download(String resourceUrl) {

        LOGGER.info("starting download from " + resourceUrl);

        return web_client
                .method(HttpMethod.GET)
                .uri(resourceUrl)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode().isError()) {
                        LOGGER.severe("error contacting " + resourceUrl);
                        return Mono.error(Exception::new);
                    }
                    return response.bodyToMono(ByteArrayResource.class);
                })
                .map(ByteArrayResource::getByteArray);
    }
}