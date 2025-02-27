package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.events.ClientScheduleEvent;
import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import br.com.uptimeowlet.backend.tasks.HealthCheckTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final I18nService i18nService;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final HistoryService historyService;
    private final HealthService healthService;
    private final ClientService clientService;

    private final ConcurrentMap<String, ScheduledFuture<?>> schedules = new ConcurrentHashMap<>();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScheduleEvent(ClientScheduleEvent event) {
        log.log(Level.INFO, i18nService.getMessage("application.logging.info.received_schedule_event",
                event.getClientId(), event.getOption()));

        switch (event.getOption()){
            case SCHEDULE :
                schedule(event.getClientId());
                break;
            case UNSCHEDULE:
                unschedule(event.getClientId());
                break;
            default:
                unschedule(event.getClientId());
                schedule(event.getClientId());
        }
    }

    public void schedule(int clientId) {
        schedule(clientId, null);
    }

    public void schedule(int clientId, Instant startTime) {
        try {
            var client = clientService.read(clientId);

            if(startTime == null)
                startTime = LocalDateTime.now().plus(client.getCheckPeriod(), ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toInstant();

            var nextKey = calcNextKey(clientId);

            var future = taskScheduler.schedule(
                    new HealthCheckTask(nextKey, client, historyService, healthService, this), startTime);

            schedules.putIfAbsent(nextKey, future);
        } catch (DataValidationException ex){}
    }

    private void unschedule(int clientId) {
        findKeysByClientId(clientId)
                .forEach(key -> unschedule(key, true));
    }

    public void unschedule(String keyTask) {
        unschedule(keyTask, false);
    }

    private void unschedule(String keyTask, boolean cancelIfIsRunning){
        if(schedules.containsKey(keyTask)){
            if(cancelIfIsRunning && !schedules.get(keyTask).isCancelled() && !schedules.get(keyTask).isDone()){
                schedules.get(keyTask).cancel(true);
            }
            schedules.remove(keyTask);
        }
    }

    private String calcNextKey(int clientId) {
        var prefixKey = String.format("%d#", clientId);

        var nextIndex = schedules.keySet().stream()
                .filter(p -> p.startsWith(prefixKey))
                .map(p -> p.substring(prefixKey.length()))
                .map(Integer::parseInt)
                .max(Integer::compare).orElse(0) + 1;

        return String.format("%d#%d", clientId, nextIndex);
    }

    private Set<String> findKeysByClientId(int clientId) {
        var prefixKey = String.format("%d#", clientId);

        return schedules.keySet().stream()
                .filter(p -> p.startsWith(prefixKey))
                .collect(Collectors.toSet());
    }

    public void unscheduleAllTasks() {
        log.log(Level.INFO, i18nService.getMessage("application.logging.info.unschedule_all_tasks"));
        schedules.keySet().forEach(keyTaks -> unschedule(keyTaks, true));
    }
}
