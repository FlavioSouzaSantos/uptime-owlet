package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.models.Client;
import br.com.uptimeowlet.backend.records.ClientInput;
import br.com.uptimeowlet.backend.repositories.ClientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Random;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientControllerTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Autowired
    ClientRepository clientRepository;

    @Order(1)
    @Test
    void shouldCreateClient() {
        var input = new ClientInput("http://localhost", "GET", "TEST",
                200, 30000L, 60000L, 2, 60000L);
        // language=GraphQL
        var document = """
        mutation createClient($input:ClientInput!){
            createClient(input: $input) {
                id
                url
                method
                name
                httpCodeForCheckIfServiceIsActive
                checkPeriod
                timeoutConnection
                maxFailureForCheckIfServiceIsInactive
                periodForNewCheckAfterFailure
                groupId
            }
        }
        """;

        graphQlTester.document(document)
                .variable("input", input)
                .execute()
                .path("createClient")
                .entity(Client.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getUrl().equals(input.url()) && p.getName().equals(input.name()));
    }

    @Order(2)
    @Test
    void shouldReadClient() {
        var client = clientRepository.findAll().iterator().next();

        // language=GraphQL
        var document = """
        query readClient($id: Int!) {
            readClient(id: $id){
                id
                name
                url
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", client.getId())
                .execute()
                .path("readClient")
                .entity(Client.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getId() == client.getId() && p.getName().equals(client.getName()) && p.getUrl().equals(client.getUrl()));
    }

    @Order(2)
    @Test
    void shouldUpdateClient() {
        var client = clientRepository.findAll().iterator().next();

        var inputClient = new ClientInput(client.getUrl(), client.getMethod(),
                String.format("%s_%d", client.getName(), new Random(10).nextInt()),
                client.getHttpCodeForCheckIfServiceIsActive(), client.getCheckPeriod(), client.getTimeoutConnection(),
                client.getMaxFailureForCheckIfServiceIsInactive(), client.getPeriodForNewCheckAfterFailure());

        // language=GraphQL
        var document = """
        mutation updateClient($id: Int!, $input: ClientInput!) {
            updateClient(id: $id, input: $input){
                id
                url
                method
                name
                httpCodeForCheckIfServiceIsActive
                checkPeriod
                timeoutConnection
                maxFailureForCheckIfServiceIsInactive
                periodForNewCheckAfterFailure
                groupId
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", client.getId())
                .variable("input", inputClient)
                .execute()
                .path("updateClient")
                .entity(Client.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getId() == client.getId() && p.getName().equals(inputClient.name()));
    }

    @Order(3)
    @Test
    void shouldDeleteClient() {
        var client = clientRepository.findAll().iterator().next();

        // language=GraphQL
        var document = """
        mutation deleteClient($id: Int!) {
            deleteClient(id: $id)
        }
        """;

        graphQlTester.document(document)
                .variable("id", client.getId())
                .execute()
                .path("deleteClient")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }
}
