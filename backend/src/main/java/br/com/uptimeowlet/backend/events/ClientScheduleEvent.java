package br.com.uptimeowlet.backend.events;

import br.com.uptimeowlet.backend.enums.ScheduleOption;
import br.com.uptimeowlet.backend.models.Client;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ClientScheduleEvent extends ApplicationEvent {
    private final int clientId;
    private final ScheduleOption option;

    public ClientScheduleEvent(Object source, int clientId, ScheduleOption option) {
        super(source);
        this.clientId = clientId;
        this.option = option;
    }
}
