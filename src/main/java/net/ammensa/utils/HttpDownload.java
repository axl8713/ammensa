package net.ammensa.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class HttpDownload {

    private static final Logger LOGGER = Logger.getLogger(HttpDownload.class.getName());

    private static final WebClient webClient = WebClient.create();

    public Mono<byte[]> download(String resourceUrl) {

        LOGGER.info("starting download from " + resourceUrl);

        return webClient
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