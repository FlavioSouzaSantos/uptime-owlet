package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.models.History;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final Validator validator;
    private final HistoryService historyService;

    @Transactional(rollbackFor = Exception.class)
    public History check(final Client client){
        var errors = validator.validateObject(client);
        if(errors.hasErrors())
            throw new DataValidationException(errors);

        var history = client.checkHealth();
        historyService.create(history);
        return history;
    }

    public List<History> latestHistories(final Client client) {
        return historyService.latestByClient(client);
    }

}
