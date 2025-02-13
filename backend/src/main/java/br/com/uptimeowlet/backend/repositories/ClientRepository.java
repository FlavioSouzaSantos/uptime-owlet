package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Integer> {
}
