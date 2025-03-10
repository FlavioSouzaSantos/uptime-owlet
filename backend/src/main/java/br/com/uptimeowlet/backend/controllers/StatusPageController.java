package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.models.Role;
import br.com.uptimeowlet.backend.models.StatusPage;
import br.com.uptimeowlet.backend.records.StatusPageInput;
import br.com.uptimeowlet.backend.services.CrudService;
import br.com.uptimeowlet.backend.services.StatusPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class StatusPageController implements CrudController<StatusPage, Integer, StatusPageInput> {

    private StatusPageService service;

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("createStatusPage")
    public StatusPage create(@Argument StatusPageInput input) {
        var statuspage = StatusPage.builder()
                .description(input.description())
                .path(input.path())
                .build();
        return service.create(statuspage);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @QueryMapping("readStatusPage")
    public StatusPage read(@Argument Integer id) {
        return service.read(id);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("updateStatusPage")
    public StatusPage update(@Argument Integer id, @Argument StatusPageInput input) {
        var statuspage = StatusPage.builder()
                .id(id)
                .description(input.description())
                .path(input.path())
                .build();
        return service.update(statuspage);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("deleteStatusPage")
    public boolean delete(@Argument Integer id) {
        service.delete(id);
        return true;
    }

    @Override
    @Autowired
    public void setService(CrudService<StatusPage, Integer> service) {
        this.service = (StatusPageService) service;
    }
}
