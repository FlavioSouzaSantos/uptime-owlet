package br.com.uptimeowlet.backend.listeners;

import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.services.ClientService;
import br.com.uptimeowlet.backend.services.ScheduleService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var scheduleService = event.getApplicationContext().getBean(ScheduleService.class);
        var clientService = event.getApplicationContext().getBean(ClientService.class);

        clientService.readAll().stream().map(Client::getId).forEach(scheduleService::schedule);
    }
}
