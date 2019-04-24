package net.ammensa.handler;

import ch.gadp.holidays.Holiday;
import ch.gadp.holidays.Holidays;
import net.ammensa.cron.MenuUpdate;
import net.ammensa.entity.Menu;
import net.ammensa.entity.MenuStatus;
import net.ammensa.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class MenuHandler {

    private static final Logger LOGGER = Logger.getLogger(MenuHandler.class.getName());
    private static final MonthDay SAN_MATTEO = MonthDay.of(Month.SEPTEMBER, 21);
    private static final List<String> IT_HOLIDAYS_REGION = Collections.singletonList("it");
    private static Clock ITALY_CLOCK = Clock.system(ZoneId.of("Europe/Rome"));

    @Autowired
    private MenuUpdate menuUpdate;
    @Autowired
    private MenuRepository menuRepository;


    public Mono<ServerResponse> serveMenu(ServerRequest request) {
        try {

            ZonedDateTime now = ZonedDateTime.now(ITALY_CLOCK);

            LOGGER.info("the time right now is " + now);

            if (isMensaClosed(now)) {
                return handleMessageResponse(request, MenuStatus.MENSA_CLOSED);
            }

            int hour = now.getHour();

            return Mono.fromCallable(menuRepository::retrieve)
                    .flatMap(optionalMenu -> optionalMenu
                            .map(menu -> {

                                LocalDate menuDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(menu.getTimestamp()), ITALY_CLOCK.getZone()).toLocalDate();

                                if (!menuDate.isEqual(now.toLocalDate())) {

                                    LOGGER.info("old menu in repository");

                                    menuRepository.delete();

                                    return handleNotAvailableResponse(request, hour);
                                }

                                return handleMenuResponse(request, menu);
                            })
                            .orElseGet(() -> {
                                LOGGER.info("no menu in repository");
                                return handleNotAvailableResponse(request, hour);
                            }));
        } catch (Exception ex) {
            return handleMessageResponse(request, MenuStatus.ERROR);
        }
    }

    private boolean isMensaClosed(ZonedDateTime now) {
        try {
            DayOfWeek todaysDayOfWeek = now.getDayOfWeek();
            List<Holiday> todaysHolidays = new Holidays(IT_HOLIDAYS_REGION).on(Date.from(now.toInstant()), IT_HOLIDAYS_REGION, Holidays.NO_OPTION);

            return todaysDayOfWeek.equals(DayOfWeek.SATURDAY) || todaysDayOfWeek.equals(DayOfWeek.SUNDAY)
                    || !todaysHolidays.isEmpty()
                    || SAN_MATTEO.equals(MonthDay.of(now.getMonth(), now.getDayOfMonth()));

        } catch (Exception ex) {
            LOGGER.severe("error checking mensa closing");
            throw new RuntimeException(ex);
        }
    }

    private Mono<ServerResponse> handleNotAvailableResponse(ServerRequest request, int finalhour) {
        if (finalhour < 9) {
            return handleMessageResponse(request, MenuStatus.TOO_EARLY);
        } else if (finalhour < 10) {
            return handleMessageResponse(request, MenuStatus.TOO_EARLY_ANYWAY);
        } else if (finalhour > 12) {
            return handleMessageResponse(request, MenuStatus.STILL_NOT_AVAILABLE);
        } else {
            return handleMessageResponse(request, MenuStatus.NOT_AVAILABLE);
        }
    }

    private Mono<ServerResponse> handleMessageResponse(ServerRequest request, MenuStatus menuStatus) {
        return new ResponseBuilder(request.headers().accept(), menuStatus)
                .withMenuStatus(menuStatus)
                .build();
    }

    private Mono<ServerResponse> handleMenuResponse(ServerRequest request, Menu menu) {

        List<MediaType> requestAcceptHeader = request.headers().accept();

        ResponseBuilder responseBuilder = new ResponseBuilder(requestAcceptHeader, MenuStatus.OK)
                .withMenu(menu);

        if (requestAcceptHeader.contains(MediaType.TEXT_HTML)) {
            ZonedDateTime tomorrowMidnight = LocalDate.now(ITALY_CLOCK.getZone()).atStartOfDay(ITALY_CLOCK.getZone()).plusDays(1);
            responseBuilder.withExpires(tomorrowMidnight);
        }

        return responseBuilder.build();
    }

    private static final class ResponseBuilder {

        private MediaType contentType;
        private String templateName;
        private Menu menu;
        private MenuStatus menuStatus;
        private ZonedDateTime expires;

        private ResponseBuilder(List<MediaType> requestAccept, MenuStatus menuStatus) {
            this.contentType = negotiateContentType(requestAccept);
            this.menuStatus = menuStatus;
        }

        private MediaType negotiateContentType(List<MediaType> requestAccept) {
            if (requestAccept.contains(MediaType.APPLICATION_JSON)) {
                return MediaType.APPLICATION_JSON;
            } else if (requestAccept.contains(MediaType.APPLICATION_XML) || requestAccept.contains(MediaType.TEXT_XML)) {
                this.templateName = "menu_xml";
                return MediaType.APPLICATION_XML;
            } else {
                this.templateName = "menu";
                return MediaType.TEXT_HTML;
            }
        }

        public ResponseBuilder withMenu(Menu menu) {
            this.menu = menu;
            return this;
        }

        public ResponseBuilder withMenuStatus(MenuStatus menuStatus) {
            this.menuStatus = menuStatus;
            return this;
        }

        public ResponseBuilder withExpires(ZonedDateTime expires) {
            this.expires = expires;
            return this;
        }

        public Mono<ServerResponse> build() {

            ServerResponse.BodyBuilder response = ServerResponse.ok()
                    .contentType(contentType);

            if (expires != null) {
                response.header(HttpHeaders.EXPIRES, expires.format(DateTimeFormatter.RFC_1123_DATE_TIME))
                        .header(HttpHeaders.CACHE_CONTROL, "public");
            }

            if (contentType == MediaType.APPLICATION_JSON) {

                Object body;
                if (menuStatus != MenuStatus.OK) {
                    body = Collections.singletonMap("info", Collections.singletonMap("status", menuStatus));
                } else {
                    body = menu;
                }
                return response.body(fromObject(body));

            } else {
                return response.render(templateName, new HashMap<String, Object>() {
                    {
                        put("status", menuStatus);
                        put("menu", menu);
                    }
                });
            }
        }
    }


    public Mono<ServerResponse> manualMenuUpdate(ServerRequest request) {
        return retrieveMenu()
                .flatMap(m -> ServerResponse.ok().body(fromObject(m)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<Menu> retrieveMenu() {
        LOGGER.info("retrieve");
        Mono<Object> objectMono = menuUpdate.updateMenu();
        return Mono
                .when(objectMono)
                .then(Mono.fromCallable(menuRepository::retrieve)
                        /* https://stackoverflow.com/a/53188485/1291616 */
                        .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty)));
    }
}
