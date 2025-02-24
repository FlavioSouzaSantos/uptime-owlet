package br.com.uptimeowlet.backend.tasks;

import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.services.HealthService;
import br.com.uptimeowlet.backend.services.HistoryService;
import lombok.extern.java.Log;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

@Log
public final class HealthCheckTask implements Runnable {
    private final Client client;
    private final HistoryService historyService;
    private final HealthService healthService;
    private final ThreadPoolTaskScheduler  taskScheduler;

    public HealthCheckTask(Client client, HistoryService historyService, HealthService healthService, ThreadPoolTaskScheduler taskScheduler) {
        this.client = client;
        this.historyService = historyService;
        this.healthService = healthService;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void run() {
        try {
            healthService.check(client);
            var histories = historyService.latestByClient(client);
            var newCheck = client.calculateNewCheck(histories);
            taskScheduler.schedule(this, newCheck.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ex){
            log.log(Level.SEVERE, ex.getMessage(), ex);
            taskScheduler.schedule(this, LocalDateTime.now().plus(client.getCheckPeriod(), ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toInstant());
        }
    }
}
