package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryRepository extends CrudRepository<History, Integer> {
    List<History> findAllByClientId(int clientId);
    Page<History> findAllByClientId(int clientId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM tb_history WHERE client_id = :clientId")
    void deleteAllByClientId(int clientId);
}
