package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.models.Role;
import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.records.ClientInput;
import br.com.uptimeowlet.backend.services.ClientService;
import br.com.uptimeowlet.backend.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class ClientController implements CrudController<Client, Integer, ClientInput> {

    private ClientService service;

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("createClient")
    public Client create(@Argument ClientInput input) {
        var client = Client.builder()
                .url(input.url())
                .method(input.method())
                .name(input.name())
                .httpCodeForCheckIfServiceIsActive(input.httpCodeForCheckIfServiceIsActive())
                .checkPeriod(input.checkPeriod())
                .timeoutConnection(input.timeoutConnection())
                .maxFailureForCheckIfServiceIsInactive(input.maxFailureForCheckIfServiceIsInactive())
                .periodForNewCheckAfterFailure(input.periodForNewCheckAfterFailure())
                .build();
        return service.create(client);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @QueryMapping("readClient")
    public Client read(@Argument Integer id) {
        return service.read(id);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("updateClient")
    public Client update(@Argument Integer id, @Argument ClientInput input) {
        var client = Client.builder()
                .id(id)
                .url(input.url())
                .method(input.method())
                .name(input.name())
                .httpCodeForCheckIfServiceIsActive(input.httpCodeForCheckIfServiceIsActive())
                .checkPeriod(input.checkPeriod())
                .timeoutConnection(input.timeoutConnection())
                .maxFailureForCheckIfServiceIsInactive(input.maxFailureForCheckIfServiceIsInactive())
                .periodForNewCheckAfterFailure(input.periodForNewCheckAfterFailure())
                .build();
        return service.update(client);
    }

    @Override
    @PreAuthorize("hasRole('"+ Role.ADMIN +"')")
    @MutationMapping("deleteClient")
    public boolean delete(@Argument Integer id) {
        service.delete(id);
        return true;
    }

    @Override
    @Autowired
    public void setService(CrudService<Client, Integer> service) {
        this.service = (ClientService) service;
    }
}
