package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.models.Group;
import br.com.uptimeowlet.backend.models.PageResultGroup;
import br.com.uptimeowlet.backend.records.PageRequestInput;
import br.com.uptimeowlet.backend.repositories.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.uptimeowlet.backend.Utils.isNullOrBlank;

@Service
public class GroupService extends CrudService<Group, Integer> {

    @Transactional(readOnly = true)
    public PageResultGroup searchGroup(PageRequestInput input){
        var pageable = input.generatePageable();

        var page = isNullOrBlank(input.search()) ?
                ((GroupRepository) repository).findAll(pageable) :
                ((GroupRepository) repository).findByNameLike(String.format("%%%s%%", input.search()), pageable);

        return new PageResultGroup(page);
    }
}
