package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.models.User;
import br.com.uptimeowlet.backend.records.ChangePasswordInput;
import br.com.uptimeowlet.backend.records.CreateUserInput;
import br.com.uptimeowlet.backend.records.TokenOutput;
import br.com.uptimeowlet.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @QueryMapping
    public boolean hasUser(){
        return service.hasUser();
    }

    @QueryMapping
    public User readUser(@Argument Integer id){
        return service.read(id);
    }

    @MutationMapping
    public User createUser(@Argument CreateUserInput input){
        return service.createUserFrom(input);
    }

    @MutationMapping
    public boolean changePassword(@Argument ChangePasswordInput input) {
        return service.changePassword(input);
    }

    @MutationMapping
    public TokenOutput auth(@Argument String login, @Argument String password) {
        return service.auth(login, password);
    }
}
