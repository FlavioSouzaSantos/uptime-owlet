package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.models.StatusPage;
import br.com.uptimeowlet.backend.records.StatusPageInput;
import br.com.uptimeowlet.backend.services.CrudService;
import br.com.uptimeowlet.backend.services.StatusPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class StatusPageController implements CrudController<StatusPage, Integer, StatusPageInput> {

    private StatusPageService service;

    @Override
    @MutationMapping("createStatusPage")
    public StatusPage create(StatusPageInput input) {
        var statuspage = StatusPage.builder()
                .description(input.description())
                .path(input.path())
                .build();
        return service.create(statuspage);
    }

    @Override
    @QueryMapping("readStatusPage")
    public StatusPage read(Integer id) {
        return service.read(id);
    }

    @Override
    @MutationMapping("updateStatusPage")
    public StatusPage update(Integer id, StatusPageInput input) {
        var statuspage = StatusPage.builder()
                .id(id)
                .description(input.description())
                .path(input.path())
                .build();
        return service.update(statuspage);
    }

    @Override
    @MutationMapping("deleteStatusPage")
    public boolean delete(Integer id) {
        service.delete(id);
        return true;
    }

    @Override
    @Autowired
    public void setService(CrudService<StatusPage, Integer> service) {
        this.service = (StatusPageService) service;
    }
}
