package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
