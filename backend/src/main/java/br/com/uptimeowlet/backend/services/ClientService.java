package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.models.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService extends CrudService<Client, Integer> {

    private final HistoryService historyService;

    @Override
    public void delete(Integer id) {
        historyService.deleteByClientId(id);
        super.delete(id);
    }
}
