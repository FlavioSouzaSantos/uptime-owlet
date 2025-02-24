package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.tasks.HealthCheckTask;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class ScheduleService implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var clientService = event.getApplicationContext().getBean(ClientService.class);
        var historyService = event.getApplicationContext().getBean(HistoryService.class);
        var healthService = event.getApplicationContext().getBean(HealthService.class);
        var threadPoolTaskScheduler = event.getApplicationContext().getBean(ThreadPoolTaskScheduler.class);

        clientService.readAll().forEach(client -> threadPoolTaskScheduler.schedule(new HealthCheckTask(client, historyService, healthService, threadPoolTaskScheduler),
                    LocalDateTime.now().plus(client.getCheckPeriod(), ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toInstant()));
    }
}
