package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import br.com.uptimeowlet.backend.models.User;
import br.com.uptimeowlet.backend.records.ChangePasswordInput;
import br.com.uptimeowlet.backend.records.CreateUserInput;
import br.com.uptimeowlet.backend.records.TokenOutput;
import br.com.uptimeowlet.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends CrudService<User, Integer> implements UserDetailsService {

    @Autowired
    private TokenService tokenService;

    @Override
    public void delete(Integer id) {
        throw new DataValidationException(i18nService.getMessage("application.crud.not_allowed_delete_user"));
    }

    @Transactional(readOnly = true)
    public boolean hasUser(){
        return repository.count() > 0L;
    }

    public User createUserFrom(CreateUserInput userInput) {
        if(hasUser()){
            throw new DataValidationException(i18nService.getMessage("application.user.already_exists"));
        }

        if(!userInput.password().equals(userInput.passwordConfirmation())){
            throw new DataValidationException(i18nService.getMessage("application.user.password_confirmation_not_equal_password"));
        }
        var user = User.builder()
                .login(userInput.login())
                .password(userInput.password())
                .build();

        return create(user);
    }

    public boolean changePassword(ChangePasswordInput input) {
        var user = read(input.id());
        if(!user.getPassword().equals(input.currentPassword())){
            throw new DataValidationException(i18nService.getMessage("application.user.invalid_current_password"));
        }
        if(user.getPassword().equals(input.newPassword())){
            throw new DataValidationException(i18nService.getMessage("application.user.new_password_should_not_equal_current_password"));
        }
        user.setPassword(input.newPassword());
        update(user);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public TokenOutput auth(String login, String password) {
        var user = ((UserRepository) repository).findFirstByLogin(login);
        if(user.isEmpty() || !user.get().getPassword().equals(password)){
            throw new DataValidationException(i18nService.getMessage("application.auth.invalid_login_and_password"));
        }
        return tokenService.create(user.get());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = ((UserRepository) repository).findFirstByLogin(username);
        if(user.isEmpty())
            throw new UsernameNotFoundException(i18nService.getMessage("application.user.not_found"));
        return null;
    }
}
