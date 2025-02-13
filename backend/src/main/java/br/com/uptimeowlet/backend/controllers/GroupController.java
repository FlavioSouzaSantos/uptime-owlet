package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.models.Group;
import br.com.uptimeowlet.backend.services.CrudService;
import br.com.uptimeowlet.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GroupController implements CrudController<Group, Integer, String> {

    private GroupService service;

    @Override
    @MutationMapping("createGroup")
    public Group create(String name) {
        var group = Group.builder()
                .name(name)
                .build();

        return service.create(group);
    }

    @Override
    @QueryMapping("readGroup")
    public Group read(Integer id) {
        return service.read(id);
    }

    @Override
    @MutationMapping("updateGroup")
    public Group update(Integer id, String name) {
        var group = Group.builder()
                .id(id)
                .name(name)
                .build();

        return service.update(group);
    }

    @Override
    @MutationMapping("deleteGroup")
    public boolean delete(Integer id) {
        service.delete(id);
        return true;
    }

    @Override
    @Autowired
    public void setService(CrudService<Group, Integer> service) {
        this.service = (GroupService) service;
    }
}
