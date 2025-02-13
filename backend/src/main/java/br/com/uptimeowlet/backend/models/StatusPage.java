package br.com.uptimeowlet.backend.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "TB_STATUS_PAGE")
public class StatusPage {

    @Id
    @EqualsAndHashCode.Include
    private int id;
    private String description;
    private String path;
}
