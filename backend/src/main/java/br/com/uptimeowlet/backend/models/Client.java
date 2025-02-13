package br.com.uptimeowlet.backend.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.MILLIS;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "TB_CLIENT")
public class Client {

    @Id
    @EqualsAndHashCode.Include
    private int id;
    @NotBlank
    private String url;
    @NotBlank
    private String method;
    @NotBlank
    private String name;
    private int httpCodeForCheckIfServiceIsActive=200;
    private long checkPeriod=5000L;
    private long timeoutConnection=30000L;
    private int maxFailureForCheckIfServiceIsInactive=2;
    private long periodForNewCheckAfterFailure=60000L;
    private Integer groupId;

    public History checkHealth() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.of(timeoutConnection, MILLIS))
                .build();

        var startTime = LocalDateTime.now();
        var history = new History(this, startTime);

        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            history.setHttpResponseCode(response.statusCode());
            history.setPingTime(Duration.between(startTime, LocalDateTime.now()).toMillis());

            if(response.statusCode() != httpCodeForCheckIfServiceIsActive){
                history.setMessage(String.format("HTTP response code expected %d but HTTP response code %d found.", httpCodeForCheckIfServiceIsActive, response.statusCode()));
            } else {
                history.setActive(true);
            }
        } catch (Exception ex) {
            history.setMessage(ex.getMessage());
        }

        return history;
    }

    public boolean checkIfServiceIsInactive(List<History> histories) {
        if(histories.size() < maxFailureForCheckIfServiceIsInactive)
            return false;

        var count = histories.stream()
                .sorted((h1, h2) -> h1.getDateTime().compareTo(h2.getDateTime()))
                .skip(histories.size() - maxFailureForCheckIfServiceIsInactive)
                .filter(p -> !p.isActive())
                .count();

        return count == maxFailureForCheckIfServiceIsInactive;
    }

    public boolean checkIfServiceIsInReactivation(List<History> histories) {
        if(histories.size() <= maxFailureForCheckIfServiceIsInactive)
            return false;

        var lastItems = histories.stream()
                .sorted((h1, h2) -> h1.getDateTime().compareTo(h2.getDateTime()))
                .skip(histories.size() - (maxFailureForCheckIfServiceIsInactive + 1))
                .toList();

        return lastItems.getLast().isActive()
                && lastItems.stream().filter(h -> !h.isActive()).count() == maxFailureForCheckIfServiceIsInactive;
    }

    public LocalDateTime calculateNewCheck(List<History> latestHistories) {
        if(latestHistories == null || latestHistories.isEmpty()) return LocalDateTime.now();

        var seconds = checkIfServiceIsInactive(latestHistories) ?
                periodForNewCheckAfterFailure / 60 :
                checkPeriod / 60;
        return latestHistories.getLast().getEndDateTime().plusSeconds(seconds);
    }
}
