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

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static net.ammensa.entity.MenuStatus.ERROR;
import static net.ammensa.entity.MenuStatus.MENSA_CLOSED;
import static net.ammensa.entity.MenuStatus.NOT_AVAILABLE;
import static net.ammensa.entity.MenuStatus.OK;
import static net.ammensa.entity.MenuStatus.STILL_NOT_AVAILABLE;
import static net.ammensa.entity.MenuStatus.STILL_TOO_EARLY;
import static net.ammensa.entity.MenuStatus.TOO_EARLY;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class MenuHandler {

    private static final Logger LOGGER = Logger.getLogger(MenuHandler.class.getName());
    private static final MonthDay SAN_MATTEO = MonthDay.of(Month.SEPTEMBER, 21);
    private static final List<String> IT_HOLIDAYS_REGION = Collections.singletonList("it");
    private static Clock italyClock = Clock.system(ZoneId.of("Europe/Rome"));

    @Autowired
    private MenuUpdate menuUpdate;
    @Autowired
    private MenuRepository menuRepository;

    public Mono<ServerResponse> serveMenu(ServerRequest request) {
        try {

            ZonedDateTime now = ZonedDateTime.now(italyClock);
            LOGGER.fine(() -> String.format("the time right now is %s", now));

            if (isMensaClosed(now)) {
                return messageResponse(request, MENSA_CLOSED);
            }

            return Mono.justOrEmpty(menuRepository.retrieve())
                    .flatMap(menu -> handleRetrievedMenu(now, menu))
                    .flatMap(menu -> menuResponse(request, menu))
                    .switchIfEmpty(Mono.defer(() ->
                            menuNotAvailableResponse(request, now.getHour()))
                    );
        } catch (Exception ex) {
            return messageResponse(request, ERROR);
        }
    }

    private boolean isMensaClosed(ZonedDateTime now) throws Exception {
        DayOfWeek todaysDayOfWeek = now.getDayOfWeek();
        List<Holiday> todaysHolidays = new Holidays(IT_HOLIDAYS_REGION).on(Date.from(now.toInstant()), IT_HOLIDAYS_REGION, Holidays.NO_OPTION);

        return todaysDayOfWeek.equals(DayOfWeek.SATURDAY) || todaysDayOfWeek.equals(DayOfWeek.SUNDAY)
                || !todaysHolidays.isEmpty()
                || SAN_MATTEO.equals(MonthDay.of(now.getMonth(), now.getDayOfMonth()));
    }

    private Mono<Menu> handleRetrievedMenu(ZonedDateTime now, Menu menu) {
        if (!menu.getDate().isEqual(now.toLocalDate())) {
            LOGGER.info("old menu in repository");
            menuRepository.delete();
            return Mono.empty();
        }
        return Mono.just(menu);
    }

    private Mono<ServerResponse> menuResponse(ServerRequest request, Menu menu) {

        List<MediaType> requestAcceptHeader = request.headers().accept();

        ResponseBuilder responseBuilder = new ResponseBuilder(requestAcceptHeader, OK)
                .withMenu(menu);

        if (requestAcceptHeader.contains(MediaType.TEXT_HTML)) {
            ZonedDateTime tomorrowMidnight = LocalDate.now(italyClock.getZone()).atStartOfDay(italyClock.getZone()).plusDays(1);
            responseBuilder.withExpires(tomorrowMidnight);
        }

        return responseBuilder.build();
    }

    private Mono<ServerResponse> menuNotAvailableResponse(ServerRequest request, int hour) {
        if (hour < 9) {
            return messageResponse(request, TOO_EARLY);
        } else if (hour < 10) {
            return messageResponse(request, STILL_TOO_EARLY);
        } else if (hour > 12) {
            return messageResponse(request, STILL_NOT_AVAILABLE);
        } else {
            return messageResponse(request, NOT_AVAILABLE);
        }
    }

    private Mono<ServerResponse> messageResponse(ServerRequest request, MenuStatus menuStatus) {
        return new ResponseBuilder(request.headers().accept(), menuStatus)
                .withMenuStatus(menuStatus)
                .build();
    }

    public Mono<ServerResponse> manualMenuUpdate() {
        return retrieveMenu()
                .flatMap(m -> ServerResponse.ok().body(fromValue(m)))
                .switchIfEmpty(ServerResponse.notFound().build());
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
                return response.body(fromValue(composeResponseBody()));
            } else {
                return response.render(templateName, Map.of("status", menuStatus, "menu", menu));
            }
        }

        private Object composeResponseBody() {
            if (menuStatus != OK) {
                return Collections.singletonMap("info", Collections.singletonMap("status", menuStatus));
            } else {
                return menu;
            }
        }
    }

    private Mono<Menu> retrieveMenu() {
        LOGGER.info("retrieve");
        return Mono
                .when(menuUpdate.updateMenu())
                .then(Mono.fromCallable(menuRepository::retrieve)
                        /* https://stackoverflow.com/a/53188485/1291616 */
                        .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty)));
    }
}