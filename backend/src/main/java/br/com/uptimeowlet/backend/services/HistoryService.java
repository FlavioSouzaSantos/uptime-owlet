package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.models.History;
import br.com.uptimeowlet.backend.repositories.HistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoryService extends CrudService<History, Integer> {

    public List<History> latestByClient(Client client) {
        var pageable = PageRequest.of(0, client.getMaxFailureForCheckIfServiceIsInactive()+1,
                Sort.by(Sort.Direction.DESC, "dateTime"));

        var page = ((HistoryRepository) repository).findAllByClientId(client.getId(), pageable);
        return page.toList();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByClientId(int clientId){
        ((HistoryRepository) repository).deleteAllByClientId(clientId);
    }
}
