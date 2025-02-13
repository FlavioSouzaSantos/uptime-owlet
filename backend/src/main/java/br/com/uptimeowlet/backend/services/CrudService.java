package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

public abstract class CrudService <T, ID> {

    protected CrudRepository<T, ID> repository;
    protected Validator validator;
    protected I18nService i18nService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public T create(T entity) {
        return save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public T update(T entity) {
        return save(entity);
    }

    protected T save(T entity) {
        var errors = validator.validateObject(entity);
        if(errors.hasErrors())
            throw new DataValidationException(errors);

        repository.save(entity);
        return entity;
    }

    @Transactional(readOnly = true)
    public T read(ID id) {
        var entity = repository.findById(id);
        return entity.orElseThrow(() -> new DataValidationException(i18nService.getMessage("application.crud.read.entity_not_found", id)));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(ID id) {
        var entity = repository.findById(id);
        entity.ifPresent(p -> repository.delete(p));
    }

    @Autowired
    public void setRepository(CrudRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Autowired
    public void setI18nService(I18nService i18nService) {
        this.i18nService = i18nService;
    }
}
