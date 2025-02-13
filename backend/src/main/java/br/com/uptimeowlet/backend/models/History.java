package br.com.uptimeowlet.backend.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table(name = "TB_HISTORY")
public class History {

    @Id
    @EqualsAndHashCode.Include
    private int id;
    private int clientId;
    private LocalDateTime dateTime;
    private int httpResponseCode;
    private long pingTime;
    private String message;
    private boolean active;

    @Transient
    private Client client;

    public History(Client client, LocalDateTime dateTime) {
        this.client = client;
        this.clientId = client != null ? client.getId() : 0;
        this.dateTime = dateTime;
    }

    public History(Client client, LocalDateTime dateTime, boolean active) {
        this(client, dateTime);
        this.active = active;
    }

    public LocalDateTime getEndDateTime() {
        return dateTime != null && pingTime > 0L ? dateTime.plusSeconds(pingTime/60) : dateTime;
    }
}
