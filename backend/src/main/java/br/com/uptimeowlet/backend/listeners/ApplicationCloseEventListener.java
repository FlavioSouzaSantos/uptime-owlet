package br.com.uptimeowlet.backend.listeners;

import br.com.uptimeowlet.backend.services.ScheduleService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ApplicationCloseEventListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        var scheduleService = event.getApplicationContext().getBean(ScheduleService.class);
        scheduleService.unscheduleAllTasks();
    }
}
