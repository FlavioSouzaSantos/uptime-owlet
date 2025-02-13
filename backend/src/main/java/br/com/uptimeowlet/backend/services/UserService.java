package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import br.com.uptimeowlet.backend.models.User;
import br.com.uptimeowlet.backend.records.ChangePasswordInput;
import br.com.uptimeowlet.backend.records.CreateUserInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends CrudService<User, Integer> {

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
}
