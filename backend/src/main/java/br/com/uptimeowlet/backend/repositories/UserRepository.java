package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findFirstByLogin(String login);
}
