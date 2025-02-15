package br.com.uptimeowlet.backend.models;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
public class PageResultGroup extends PageResult<Group> {

    public PageResultGroup(Page<Group> page) {
        super(page);
    }
}
