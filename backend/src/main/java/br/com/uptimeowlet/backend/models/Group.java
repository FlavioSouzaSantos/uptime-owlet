package br.com.uptimeowlet.backend.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "TB_GROUP")
public class Group {

    @Id
    @EqualsAndHashCode.Include
    private int id;
    private String name;
}
