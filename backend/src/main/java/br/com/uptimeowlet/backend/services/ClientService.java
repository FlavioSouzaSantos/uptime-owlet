package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.enums.ScheduleOption;
import br.com.uptimeowlet.backend.events.ClientScheduleEvent;
import br.com.uptimeowlet.backend.models.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ClientService extends CrudService<Client, Integer> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final HistoryService historyService;

    @Override
    public Client create(Client entity) {
        var client = super.create(entity);
        applicationEventPublisher.publishEvent(new ClientScheduleEvent(this, client.getId(), ScheduleOption.SCHEDULE));
        return client;
    }

    @Override
    public Client update(Client entity) {
        var client = super.update(entity);
        applicationEventPublisher.publishEvent(new ClientScheduleEvent(this, client.getId(), ScheduleOption.RESCHEDULE));
        return client;
    }

    @Override
    public void delete(Integer id) {
        historyService.deleteByClientId(id);
        super.delete(id);
        applicationEventPublisher.publishEvent(new ClientScheduleEvent(this, id, ScheduleOption.UNSCHEDULE));
    }

    @Transactional(readOnly = false)
    public List<Client> readAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .toList();
    }
}
