package br.com.uptimeowlet.backend.repositories;

import br.com.uptimeowlet.backend.models.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GroupRepository extends CrudRepository<Group, Integer>, PagingAndSortingRepository<Group, Integer> {
    Page<Group> findByNameLike(String name, Pageable pageable);
}
