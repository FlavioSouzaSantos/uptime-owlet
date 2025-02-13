package br.com.uptimeowlet.backend.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "TB_USER")
public class User {

    @Id
    @EqualsAndHashCode.Include
    private int id;
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
