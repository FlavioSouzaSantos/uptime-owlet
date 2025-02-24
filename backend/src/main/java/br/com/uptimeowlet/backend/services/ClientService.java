package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.models.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ClientService extends CrudService<Client, Integer> {

    private final HistoryService historyService;

    @Override
    public void delete(Integer id) {
        historyService.deleteByClientId(id);
        super.delete(id);
    }

    @Transactional(readOnly = false)
    public List<Client> readAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .toList();
    }
}
