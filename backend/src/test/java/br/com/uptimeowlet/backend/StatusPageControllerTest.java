package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.models.StatusPage;
import br.com.uptimeowlet.backend.repositories.StatusPageRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Random;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatusPageControllerTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Autowired
    StatusPageRepository statusPageRepository;

    @Order(1)
    @Test
    void shouldCreateStatusPage() {
        var description = "TEST";
        var path = "/test";
        // language=GraphQL
        var document = """
        mutation createStatusPage($description: String!, $path: String!){
            createStatusPage(input: {description: $description, path: $path}) {
                id
                description
                path
            }
        }
        """;

        graphQlTester.document(document)
                .variable("description", description)
                .variable("path", path)
                .execute()
                .path("createStatusPage")
                .entity(StatusPage.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getDescription().equals(description) && p.getPath().equals(path));
    }

    @Order(2)
    @Test
    void shouldReadStatusPage() {
        var statusPage = statusPageRepository.findAll().iterator().next();

        // language=GraphQL
        var document = """
        query readStatusPage($id: Int!) {
            readStatusPage(id: $id){
                id
                description
                path
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", statusPage.getId())
                .execute()
                .path("readStatusPage")
                .entity(StatusPage.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getId() == statusPage.getId());
    }

    @Order(2)
    @Test
    void shouldUpdateStatusPage() {
        var statusPage = statusPageRepository.findAll().iterator().next();
        var newDescription = String.format("%s_%d", statusPage.getDescription(), new Random(10).nextInt());

        // language=GraphQL
        var document = """
        mutation updateStatusPage($id: Int!, $description: String!, $path: String!) {
            updateStatusPage(id: $id, input: {description: $description, path: $path}){
                id
                description
                path
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", statusPage.getId())
                .variable("description", newDescription)
                .variable("path", statusPage.getPath())
                .execute()
                .path("updateStatusPage")
                .entity(StatusPage.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getId() == statusPage.getId() && p.getDescription().equals(newDescription));
    }

    @Order(3)
    @Test
    void shouldDeleteStatusPage() {
        var statusPage = statusPageRepository.findAll().iterator().next();

        // language=GraphQL
        var document = """
        mutation deleteStatusPage($id: Int!) {
            deleteStatusPage(id: $id)
        }
        """;

        graphQlTester.document(document)
                .variable("id", statusPage.getId())
                .execute()
                .path("deleteStatusPage")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }
}
