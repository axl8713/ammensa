package net.ammensa.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartMenuUpdater implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private MenuUpdate menuHandler;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        menuHandler.updateMenu().subscribe();
    }
}