package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Integer> {

    Optional<Token> findFirstByTokenId(String tokenId);
}
