package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.models.Group;
import br.com.uptimeowlet.backend.models.PageResultGroup;
import br.com.uptimeowlet.backend.models.Role;
import br.com.uptimeowlet.backend.repositories.GroupRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Random;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupControllerTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Autowired
    GroupRepository groupRepository;

    @Order(1)
    @Test
    @WithMockUser(roles = {Role.ADMIN})
    void shouldCreateGroup() {
        var name = "TEST";
        // language=GraphQL
        var document = """
        mutation createGroup($name: String!){
            createGroup(name: $name) {
                id
                name
            }
        }
        """;

        graphQlTester.document(document)
                .variable("name", name)
                .execute()
                .path("createGroup")
                .entity(Group.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getName().equals(name));
    }

    @Order(2)
    @Test
    @WithMockUser(roles = {Role.ADMIN})
    void shouldReadGroup() {
        var group = groupRepository.findAll().iterator().next();

        // language=GraphQL
        var document = """
        query readGroup($id: Int!) {
            readGroup(id: $id){
                id
                name
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", group.getId())
                .execute()
                .path("readGroup")
                .entity(Group.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getId() == group.getId());
    }

    @Order(2)
    @Test
    @WithMockUser(roles = {Role.ADMIN})
    void shouldUpdateGroup() {
        var group = groupRepository.findAll().iterator().next();
        var newName = String.format("%s_%d", group.getName(), new Random(10).nextInt());

        // language=GraphQL
        var document = """
        mutation updateGroup($id: Int!, $name: String!) {
            updateGroup(id: $id, name: $name){
                id
                name
            }
        }
        """;

        graphQlTester.document(document)
                .variable("id", group.getId())
                .variable("name", newName)
                .execute()
                .path("updateGroup")
                .entity(Group.class)
                .satisfies(Assertions::assertNotNull)
                .matches(p -> p.getId() == group.getId() && p.getName().equals(newName));
    }

    @Order(3)
    @Test
    @WithMockUser(roles = {Role.ADMIN})
    void shouldSearchGroup() {
        var groupList = StreamSupport.stream(groupRepository.findAll().spliterator(), false).toList();
        var group = groupList.getFirst();
        var letter = group.getName().substring(0, 1);

        // language=GraphQL
        var document = """
        query searchGroup($search: String, $pageSize: Int!) {
            searchGroup(input: {search: $search, pageNumber: 1, pageSize: $pageSize}){
                pageSize
                content {
                    id
                    name
                }
                currentPage
                totalPages
            }
        }
        """;

        graphQlTester.document(document)
                .variable("search", letter)
                .variable("pageSize", 10)
                .execute()
                .path("searchGroup")
                .entity(PageResultGroup.class)
                .satisfies( p -> {
                    assertNotNull(p);
                    assertNotNull(p.getContent());
                    assertEquals(1, p.getCurrentPage());
                    assertEquals(1, p.getPageSize());
                    assertEquals(1, p.getTotalPages());
                });

        graphQlTester.document(document)
                .variable("pageSize", groupList.size())
                .execute()
                .path("searchGroup")
                .entity(PageResultGroup.class)
                .satisfies( p -> {
                    assertNotNull(p);
                    assertNotNull(p.getContent());
                    assertEquals(groupList.size(), p.getPageSize());
                    assertEquals(1, p.getCurrentPage());
                    assertEquals(1, p.getTotalPages());
                });
    }

    @Order(4)
    @Test
    @WithMockUser(roles = {Role.ADMIN})
    void shouldDeleteGroup() {
        var group = groupRepository.findAll().iterator().next();

        // language=GraphQL
        var document = """
        mutation deleteGroup($id: Int!) {
            deleteGroup(id: $id)
        }
        """;

        graphQlTester.document(document)
                .variable("id", group.getId())
                .execute()
                .path("deleteGroup")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }
}
