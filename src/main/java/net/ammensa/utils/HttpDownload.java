package net.ammensa.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class HttpDownload {

    private static WebClient webClient = WebClient.create();

    private static final Logger log = Logger.getLogger("debug");

    public Mono<byte[]> download(String resourceUrl) {

        log.info("starting download from " + resourceUrl);

        return webClient
                .method(HttpMethod.GET)
                .uri(resourceUrl)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode().isError()) {
                        log.severe("error contacting " + resourceUrl);
                        return Mono.error(Exception::new);
                    }
                    return response.bodyToMono(ByteArrayResource.class);
                })
                .map(ByteArrayResource::getByteArray);
    }
}