package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.models.Group;
import br.com.uptimeowlet.backend.models.PageResultGroup;
import br.com.uptimeowlet.backend.models.Role;
import br.com.uptimeowlet.backend.records.PageRequestInput;
import br.com.uptimeowlet.backend.services.CrudService;
import br.com.uptimeowlet.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class GroupController implements CrudController<Group, Integer, String> {

    private GroupService service;

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("createGroup")
    public Group create(@Argument String name) {
        var group = Group.builder()
                .name(name)
                .build();

        return service.create(group);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @QueryMapping("readGroup")
    public Group read(@Argument Integer id) {
        return service.read(id);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("updateGroup")
    public Group update(@Argument Integer id, @Argument String name) {
        var group = Group.builder()
                .id(id)
                .name(name)
                .build();

        return service.update(group);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("deleteGroup")
    public boolean delete(@Argument Integer id) {
        service.delete(id);
        return true;
    }

    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @QueryMapping
    public PageResultGroup searchGroup(@Argument PageRequestInput input) {
        return service.searchGroup(input);
    }

    @Override
    @Autowired
    public void setService(CrudService<Group, Integer> service) {
        this.service = (GroupService) service;
    }
}
