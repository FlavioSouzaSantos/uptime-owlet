package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.models.History;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.logging.Level;

@Log
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
        if(history.isActive()){
            log.log(Level.INFO, String.format("Health check for client ID %d did at %s with a duration %s milliseconds.",
                    history.getClientId(), history.getDateTime(), history.getPingTime()));
        } else {
            log.log(Level.WARNING, String.format("Health check for client ID %d failed at %s.",
                    history.getClientId(), history.getDateTime()));
        }

        historyService.create(history);
        return history;
    }

    public List<History> latestHistories(final Client client) {
        return historyService.latestByClient(client);
    }

}
