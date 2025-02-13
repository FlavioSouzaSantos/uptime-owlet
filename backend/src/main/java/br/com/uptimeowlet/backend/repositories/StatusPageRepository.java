package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.StatusPage;
import org.springframework.data.repository.CrudRepository;

public interface StatusPageRepository extends CrudRepository<StatusPage, Integer> {
}
