package net.ammensa.cron;

import net.ammensa.utils.HttpDownload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ApplicationWakeUp {

    private static final Logger LOGGER = Logger.getLogger(ApplicationWakeUp.class.getName());

    @Autowired
    private HttpDownload httpDownload;
    @Value("${server.port}")
    private int serverPort;

    @Scheduled(cron = "0 */2 * * * *")
    public void wakeUpApplication() {
        LOGGER.info("starting wake-up");
        httpDownload.download("http://localhost:" + serverPort).subscribe(a -> LOGGER.info("wake-up complete"));
    }
}
