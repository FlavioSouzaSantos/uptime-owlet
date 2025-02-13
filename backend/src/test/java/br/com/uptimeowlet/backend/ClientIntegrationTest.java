package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.exceptions.DataValidationException;
import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.repositories.ClientRepository;
import br.com.uptimeowlet.backend.repositories.HistoryRepository;
import br.com.uptimeowlet.backend.services.ClientService;
import br.com.uptimeowlet.backend.services.HealthService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ClientIntegrationTest {

	@Autowired
	private HealthService healthService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Order(1)
	@Test
	void shouldCreateNewClient() {
		var client = new Client();
		client.setUrl("https://www.google.com/");
		client.setName("Google site");
		client.setHttpCodeForCheckIfServiceIsActive(200);
		client.setMethod(HttpMethod.GET.name());

		client = clientService.create(client);
		assertNotNull(client);
		assertThat(client.getId()).isGreaterThan(0);
	}

	@Test
	void shouldNotCreateNewClient() {
		var client = new Client();
		client.setName("Client Test");
		assertThrows(DataValidationException.class, () -> clientService.create(client));
	}

	@Test
	void shouldSuccessHealth() {
		var client = clientRepository.findAll().iterator().next();
		healthService.check(client);
		var histories = historyRepository.findAllByClientId(client.getId());
		assertFalse(histories.isEmpty());
		assertEquals(client.getHttpCodeForCheckIfServiceIsActive(), histories.getLast().getHttpResponseCode());
	}

	@Test
	void shouldFlowTheServiceHealthCheck() {
		var client = clientRepository.findAll().iterator().next();
		assertNotNull(client);

		healthService.check(client);
		var histories = healthService.latestHistories(client);
		assertFalse(client.checkIfServiceIsInactive(histories));

		var nextCheckDateTime = client.calculateNewCheck(histories);
		assertEquals(histories.getLast().getEndDateTime().plusSeconds(client.getCheckPeriod()/60),
				nextCheckDateTime);

		client.setMethod(HttpMethod.POST.name());
		healthService.check(client);
		healthService.check(client);
		histories = healthService.latestHistories(client);
		assertTrue(client.checkIfServiceIsInactive(histories));

		nextCheckDateTime = client.calculateNewCheck(histories);
		assertEquals(histories.getLast().getEndDateTime().plusSeconds(client.getPeriodForNewCheckAfterFailure()/60),
				nextCheckDateTime);
	}

}
