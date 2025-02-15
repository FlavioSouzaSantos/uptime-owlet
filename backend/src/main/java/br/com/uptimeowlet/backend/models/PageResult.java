package br.com.uptimeowlet.backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class PageResult<T> {
    protected List<T> content;
    protected int currentPage;
    protected int totalPages;
    protected int pageSize;

    public PageResult(Page<T> page) {
        content = page.getContent();
        currentPage = page.getNumber() + 1;
        pageSize = page.getContent().size();
        totalPages = page.getTotalPages();
    }
}
