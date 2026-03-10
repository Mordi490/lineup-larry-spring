package dev.mordi.lineuplarry.lineup_larry_backend.user;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.json.JsonCompareMode;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupIdTitleDTO;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupWithAuthorDTO;
import dev.mordi.lineuplarry.lineup_larry_backend.shared.RestIntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureRestTestClient
public class UserIntegrationTest extends RestIntegrationTestSupport {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Test
    void successfulGetAll() {
        // expect the users insert by the test-data.sql
        List<User> expectedUserList = List.of(new User(1L, "userOne"), new User(2L, "userTwo"),
                new User(3L, "userThree"), new User(4L, "userFour"), new User(5L, "userFive"));

        List<User> users = getOkBody("/api/users", new ParameterizedTypeReference<List<User>>() {
        });

        assertThat(users).isEqualTo(expectedUserList);
    }

    @Test
    void successfulGetById() {
        var response = getOkBody("/api/users/1", new ParameterizedTypeReference<Optional<User>>() {
        });

        User expectedUser = new User(1L, "userOne");

        assertThat(response).isNotEmpty();
        assertThat(response.get()).isEqualTo(expectedUser);
    }

    @Test
    void getByIdOnNonexistentId() {
        client.get()
                .uri("/api/users/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                          "status": 404,
                          "title": "User not found",
                          "code": "USER_NOT_FOUND",
                          "detail": "User with id: '999' was not found",
                          "instance": "/api/users/999",
                          "type": "https://lineup-larry.dev/problems/users/not-found"
                        }
                            """, JsonCompareMode.LENIENT);
    }

    @Test
    void failGetByIdOnString() {
        // TODO: reconsider this test case
        client.get()
                .uri("/api/users/someString")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_PARAMETER",
                          "detail": "Invalid value 'someString' for parameter 'id'. Expected type: 'Long'",
                          "instance": "/api/users/someString",
                          "type": "https://lineup-larry.dev/problems/request/invalid-parameter"
                        }
                            """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulCreate() {
        User newUser = new User(null, "Bobby");

        var response = client.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.id()).isEqualTo(101);
    }

    // TODO: rethink these test cases
    // They validation could maybe be elsewhere
    // Or at least use parameterized test
    @Test
    void successfulCreateWithoutId() {
        String userWithNoIdJsonTemplate = """
                {"username":"Bobby"}\
                """;

        var response = client.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userWithNoIdJsonTemplate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.id()).isEqualTo(101);
    }

    // TODO: improve this error details, it looks bad
    // "detail": "username: username cannot be empty; username: username cannot be
    // null; username: username cannot be blank",
    // the order is random
    @Test
    void failCreateOnNullUsername() {
        String userWithNullUsernameJsonTemplate = """
                {"username":null}\
                """;

        client.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userWithNullUsernameJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/users",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """,
                        JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnEmptyUsername() {
        String userWithEmptyUsernameJson = """
                {"username":""}\
                """;

        client.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userWithEmptyUsernameJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/users",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """,
                        JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnBlankUsername() {
        String userWithBlankUsernameJson = """
                {"username":"  "}\
                """;

        client.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userWithBlankUsernameJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/users",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """,
                        JsonCompareMode.LENIENT);
    }

    @Test
    void successfulUpdate() {
        // "userOne" is the initial name
        User updatedUser = new User(1L, "Bobby");

        client.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        client.get()
                .uri("/api/users/1")
                .exchange()
                .expectBody()
                .json("""
                        {
                        "id": 1,
                        "username": "Bobby"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void failUpdateOnNullUsername() {
        User updatedUser = new User(1L, null);

        client.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/users/1",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnEmptyUsername() {
        User updatedUser = new User(1L, "");

        client.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/users/1",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnBlankUsername() {
        User updatedUser = new User(1L, "   ");

        client.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/users/1",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // deleteUser
    @Test
    void successfulDeleteUserWithNoLineups() {
        client.delete()
                .uri("/api/users/4")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    // When a user gets deleted the lineups created by the user will also get
    // deleted
    // for reference the userId 2 has two lineups, with id 2 and 3.
    @Test
    void successfulDeleteUserWithLineups() {
        // confirm that the user does have lineups
        List<LineupWithAuthorDTO> lineups = getOkBody("/api/lineups/user/2",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        assertThat(lineups).isNotEmpty();
        assertThat(lineups).extracting(LineupWithAuthorDTO::id)
                .contains(2L, 3L);

        // delete the user
        client.delete()
                .uri("/api/users/2")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        // confirm that the user has been deleted
        client.get()
                .uri("/api/users/2")
                .exchange()
                .expectStatus().isNotFound();

        // confirm that the lineups also have been deleted
        client.get()
                .uri("/api/lineups/2")
                .exchange()
                .expectStatus().isNotFound();

        client.get()
                .uri("/api/lineups/3")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void failDeleteOnNonexistentId() {
        // we'd expect to receive a 404 w/msg "no user associated with id XZY"-esq
        // for reference, userId: 2 has lineups (ids) 2 and 3.
        client.delete()
                .uri("/api/users/222")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json("""
                        {
                          "status": 404,
                          "title": "User not found",
                          "code": "USER_NOT_FOUND",
                          "instance": "/api/users/222",
                          "detail": "User with id: '222' was not found",
                          "type": "https://lineup-larry.dev/problems/users/not-found"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // unsuccessful delete TODO: once auth has been impl

    // getUserSummary
    @Test
    void successfulGetUserSummary() {
        var response = getOkBody("/api/users/summary/3",
                new ParameterizedTypeReference<UserSummaryDTO>() {
                });

        // expeceted resutls:
        List<LineupIdTitleDTO> recentlyCreatedLineups = List.of(
                new LineupIdTitleDTO(22L, "titleFour"), new LineupIdTitleDTO(23L, "titleFour"),
                new LineupIdTitleDTO(24L, "titleFour"), new LineupIdTitleDTO(25L, "titleFour"),
                new LineupIdTitleDTO(26L, "titleFour"));

        List<LineupIdTitleDTO> mostLikedLineups = List.of(new LineupIdTitleDTO(22L, "titleFour"),
                new LineupIdTitleDTO(20L, "titleFour"), new LineupIdTitleDTO(14L, "titleFour"),
                new LineupIdTitleDTO(11L, "sick pop flash"),
                new LineupIdTitleDTO(4L, "lineupFour"));

        List<LineupIdTitleDTO> recentlyLikedLineups = List.of(new LineupIdTitleDTO(1L, "lineupOne"),
                new LineupIdTitleDTO(20L, "titleFour"), new LineupIdTitleDTO(9L, "teleport thingy"),
                new LineupIdTitleDTO(15L, "titleFour"), new LineupIdTitleDTO(22L, "titleFour"));

        assertThat(response.mostLikedLineups()).isEqualTo(mostLikedLineups);
        assertThat(response.recentLineups()).isEqualTo(recentlyCreatedLineups);
        assertThat(response.recentlyLikedLineups()).isEqualTo(recentlyLikedLineups);

    }

    @Test
    void getUserSummaryOnUserWithNoData() {
        var response = getOkBody("/api/users/summary/5",
                new ParameterizedTypeReference<UserSummaryDTO>() {
                });

        assertThat(response.userId()).isEqualTo(5);
        assertThat(response.username()).isEqualTo("userFive");
        assertThat(response.recentLineups()).isEmpty();
        assertThat(response.mostLikedLineups()).isEmpty();
        assertThat(response.recentlyLikedLineups()).isEmpty();
    }

    @Test
    void getUserSummaryOnNonexistentUser() {
        client.get()
                .uri("/api/users/summary/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json("""
                        {
                          "status": 404,
                          "title": "User not found",
                          "code": "USER_NOT_FOUND",
                          "instance": "/api/users/summary/999",
                          "detail": "User with id: '999' was not found",
                          "type": "https://lineup-larry.dev/problems/users/not-found"
                        }
                        """, JsonCompareMode.LENIENT);
    }
}
