package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.models.User;
import br.com.uptimeowlet.backend.repositories.UserRepository;
import br.com.uptimeowlet.backend.services.I18nService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Autowired
    I18nService i18nService;

    @Autowired
    UserRepository userRepository;

    @Order(1)
    @Test
    void shouldNotHaveUser() {
        // language=GraphQL
        var document = """
        query {
            hasUser
        }
        """;

        graphQlTester.document(document)
                .execute()
                .path("hasUser")
                .entity(Boolean.class)
                .satisfies(Assertions::assertFalse);
    }

    @Order(2)
    @Test
    void shouldCreateUser() {
        // language=GraphQL
        var document = """
        mutation {
            createUser(input: {login: "admin", password: "123", passwordConfirmation: "123"}){
                id
                login
            }
        }
        """;

        graphQlTester.document(document)
                .execute()
                .path("createUser")
                .entity(User.class)
                .satisfies(Assertions::assertNotNull);
    }

    @Order(2)
    @Test
    void shouldValidateConfirmationPassword() {
        // language=GraphQL
        var document = """
        mutation {
            createUser(input: {login: "admin", password: "123", passwordConfirmation: "456"}){
                id
                login
            }
        }
        """;

        graphQlTester.document(document)
                .execute()
                .errors()
                .expect(p -> {
                    assertEquals(i18nService.getMessage("application.user.password_confirmation_not_equal_password"),
                            p.getMessage());
                    return true;
                });
    }

    @Order(3)
    @Test
    void shouldReadUser() {
        var user = userRepository.findAll().iterator().next();
        // language=GraphQL
        var document = """
        query readUser($id: Int!){
            readUser(id: $id){
                id
                login
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", user.getId())
                .execute()
                .path("readUser")
                .entity(User.class)
                .satisfies(u -> assertEquals(user.getId(), u.getId()));
    }

    @Order(3)
    @Test
    void shouldHasUser() {
        // language=GraphQL
        var document = """
        query {
            hasUser
        }
        """;
        graphQlTester.document(document)
                .execute()
                .path("hasUser")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }

    @Order(4)
    @Test
    void shouldNotChangePassword() {
        var user = userRepository.findAll().iterator().next();
        // language=GraphQL
        var document = """
        mutation changePassword($id: Int!, $currentPassword: String!, $newPassword: String!){
            changePassword(input: {id: $id, currentPassword: $currentPassword, newPassword: $newPassword})
        }
        """;

        graphQlTester.document(document)
                .variable("id", user.getId())
                .variable("currentPassword", user.getPassword())
                .variable("newPassword", user.getPassword())
                .execute()
                .errors()
                .expect(p -> {
                    assertEquals(i18nService.getMessage("application.user.new_password_should_not_equal_current_password"),
                            p.getMessage());
                    return true;
                });
    }

    @Order(4)
    @Test
    void shouldChangePassword() {
        var user = userRepository.findAll().iterator().next();
        // language=GraphQL
        var document = """
        mutation changePassword($id: Int!, $currentPassword: String!, $newPassword: String!){
            changePassword(input: {id: $id, currentPassword: $currentPassword, newPassword: $newPassword})
        }
        """;

        graphQlTester.document(document)
                .variable("id", user.getId())
                .variable("currentPassword", user.getPassword())
                .variable("newPassword", String.format("%s_%d", user.getPassword(), new Random(10).nextInt()))
                .execute()
                .path("changePassword")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }
}
