package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.models.History;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHealthCheckTest {

    @Test
    void shouldCheckEndpointHealth() {
        var client = createClientForTest();
        var history = client.checkHealth();
        assertNotNull(history);
    }

    @Test
    void shouldNotCheckEndpointHealth() {
        var client = createClientForTest();
        client.setMethod(HttpMethod.POST.name());

        var history = client.checkHealth();
        assertNotNull(history.getMessage());
        assertNotEquals(200, history.getHttpResponseCode());
        assertFalse(history.isActive());
    }

    @Test
    void shouldIsInactiveService() {
        var client = createClientForTest(2);

        var startTime = LocalDateTime.now();
        var histories = Arrays.asList(
          new History(client, startTime.plusMinutes(1L), true),
          new History(client, startTime.plusMinutes(2L), false),
          new History(client, startTime.plusMinutes(3L), false)
        );
        assertTrue(client.checkIfServiceIsInactive(histories));
    }

    @Test
    void shouldIsActiveService() {
        var client = createClientForTest(1);

        var startTime = LocalDateTime.now();
        var histories = Arrays.asList(
                new History(client, startTime.plusMinutes(1L), true),
                new History(client, startTime.plusMinutes(2L), true),
                new History(client, startTime.plusMinutes(3L), true)
        );
        assertFalse(client.checkIfServiceIsInactive(histories));
    }

    @Test
    void shouldIsReactivationOfTheService() {
        var client = createClientForTest(2);

        var startTime = LocalDateTime.now();
        var histories = Arrays.asList(
                new History(client, startTime.plusMinutes(1L), true),
                new History(client, startTime.plusMinutes(2L), false),
                new History(client, startTime.plusMinutes(3L), false),
                new History(client, startTime.plusMinutes(4L), true)
        );

        assertTrue(client.checkIfServiceIsInReactivation(histories));
    }

    @Test
    void shouldIsNotReactivationOfTheService() {
        var client = createClientForTest(2);

        var startTime = LocalDateTime.now();
        var histories = Arrays.asList(
                new History(client, startTime.plusMinutes(1L), true),
                new History(client, startTime.plusMinutes(2L), false),
                new History(client, startTime.plusMinutes(3L), true),
                new History(client, startTime.plusMinutes(4L), true)
        );

        assertFalse(client.checkIfServiceIsInReactivation(histories));
    }

    @Test
    void shouldCalculateNextDateTimeCheck() {
        var client = createClientForTest();

        var startTime = LocalDateTime.now();
        var histories = Arrays.asList(
                new History(client, startTime.plusMinutes(1L), true),
                new History(client, startTime.plusMinutes(2L), false),
                new History(client, startTime.plusMinutes(3L), true),
                new History(client, startTime.plusMinutes(4L), true)
        );

        assertEquals(startTime.plusMinutes(4L).plusSeconds(client.getCheckPeriod()/60),
                client.calculateNewCheck(histories));

        histories = Arrays.asList(
                new History(client, startTime.plusMinutes(1L), true),
                new History(client, startTime.plusMinutes(2L), false),
                new History(client, startTime.plusMinutes(3L), true),
                new History(client, startTime.plusMinutes(4L), true),
                new History(client, startTime.plusMinutes(5), false),
                new History(client, startTime.plusMinutes(6), false)
        );
        assertEquals(startTime.plusMinutes(6).plusSeconds(client.getPeriodForNewCheckAfterFailure()/60),
                client.calculateNewCheck(histories));

        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                client.calculateNewCheck(Collections.emptyList()).truncatedTo(ChronoUnit.SECONDS));
    }

    private Client createClientForTest(int maxFailureForCheckIfServiceIsInactive) {
        var client = createClientForTest();
        client.setMaxFailureForCheckIfServiceIsInactive(maxFailureForCheckIfServiceIsInactive);
        return client;
    }

    private Client createClientForTest(){
        var client = new Client();
        client.setId(1);
        client.setUrl("https://www.google.com/");
        client.setName("Google site");
        client.setHttpCodeForCheckIfServiceIsActive(200);
        client.setMethod(HttpMethod.GET.name());
        return client;
    }
}
