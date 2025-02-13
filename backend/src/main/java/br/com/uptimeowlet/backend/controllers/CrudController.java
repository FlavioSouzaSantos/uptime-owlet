package br.com.uptimeowlet.backend.controllers;

import br.com.uptimeowlet.backend.services.CrudService;

public interface CrudController<T, ID, INPUT> {

    T create(INPUT input);

    T read(ID id);

    T update(ID id, INPUT input);

    boolean delete(ID id);

    void setService(CrudService<T, ID> service);
}
