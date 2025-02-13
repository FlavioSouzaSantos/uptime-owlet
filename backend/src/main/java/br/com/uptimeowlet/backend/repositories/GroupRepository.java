package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Integer> {
}
