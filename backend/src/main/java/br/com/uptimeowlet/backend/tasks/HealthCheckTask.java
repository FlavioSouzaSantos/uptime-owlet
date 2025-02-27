package br.com.uptimeowlet.backend.tasks;

import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.services.HealthService;
import br.com.uptimeowlet.backend.services.HistoryService;
import br.com.uptimeowlet.backend.services.ScheduleService;
import lombok.extern.java.Log;

import java.time.Instant;
import java.time.ZoneId;
import java.util.logging.Level;

@Log
public final class HealthCheckTask implements Runnable {
    private final String keyTask;
    private final Client client;
    private final HistoryService historyService;
    private final HealthService healthService;
    private final ScheduleService scheduleService;

    public HealthCheckTask(String keyTask, Client client, HistoryService historyService, HealthService healthService, ScheduleService scheduleService) {
        this.keyTask = keyTask;
        this.client = client;
        this.historyService = historyService;
        this.healthService = healthService;
        this.scheduleService = scheduleService;
    }

    @Override
    public void run() {
        Instant startTime = null;
        try {
            healthService.check(client);
            var histories = historyService.latestByClient(client);
            var newCheck = client.calculateNewCheck(histories);
            startTime = newCheck.atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception ex){
            log.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            scheduleService.unschedule(keyTask);
            if(startTime != null)
                scheduleService.schedule(client.getId(), startTime);
            else scheduleService.schedule(client.getId());
        }
    }
}
