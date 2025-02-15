package br.com.uptimeowlet.backend.records;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static br.com.uptimeowlet.backend.Utils.isNullOrBlank;

public record PageRequestInput(String search, String sortField, Boolean descOrdering, int pageNumber, int pageSize) {
    public Pageable generatePageable(){
        if(!isNullOrBlank(sortField)){
            return PageRequest.of(pageNumber-1, pageSize,
                    Sort.by(descOrdering != null && descOrdering ? Sort.Direction.DESC : Sort.Direction.ASC, sortField));
        } else {
            return PageRequest.of(pageNumber-1, pageSize);
        }
    }
}
