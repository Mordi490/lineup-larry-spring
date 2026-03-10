package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import java.util.List;

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

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.shared.RestIntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureRestTestClient
public class LineupIntegrationTest extends RestIntegrationTestSupport {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Test
    void successfulGetAll() {
        List<LineupWithAuthorDTO> expectedArray = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"),
                new LineupWithAuthorDTO(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree",
                        2L, null, null, "userTwo"),
                new LineupWithAuthorDTO(4L, Agent.CYPHER, Map.SUNSET, "lineupFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(7L, Agent.CHAMBER, Map.SPLIT, "awp crutch",
                        "filler text here", 3L, null, null, "userThree"),
                new LineupWithAuthorDTO(8L, Agent.BREACH, Map.FRACTURE, "some flash",
                        "even more filler text here", 1L, null, null, "userOne"),
                new LineupWithAuthorDTO(9L, Agent.YORU, Map.HAVEN, "teleport thingy",
                        "good for post plant", 2L, null, null, "userTwo"),
                new LineupWithAuthorDTO(10L, Agent.PHOENIX, Map.LOTUS, "cheeky flash",
                        "then click heads", 1L, null, null, "userOne"),
                new LineupWithAuthorDTO(11L, Agent.SKYE, Map.SPLIT, "sick pop flash", "then dog",
                        3L, null, null, "userThree"),
                new LineupWithAuthorDTO(12L, Agent.VYSE, Map.BREEZE, "click heads",
                        "just click the head", 3L, null, null, "userThree"),
                new LineupWithAuthorDTO(13L, Agent.OMEN, Map.SUNSET, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(14L, Agent.VIPER, Map.SPLIT, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(15L, Agent.SAGE, Map.ICEBOX, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(16L, Agent.RAZE, Map.BREEZE, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(17L, Agent.ASTRA, Map.ICEBOX, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(18L, Agent.KAYO, Map.ASCENT, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(19L, Agent.NEON, Map.FRACTURE, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(20L, Agent.FADE, Map.LOTUS, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"));

        var response = client.get()
                .uri("/api/lineups")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedArray);
    }

    @Test
    void successfulSearchByTitleWithMatches() {
        var response = client.get()
                .uri("/api/lineups?title=same+name")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                })
                .returnResult()
                .getResponseBody();

        List<LineupWithAuthorDTO> expectedArray = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedArray);
    }

    // Reconsider if this is even a good practice
    @Test
    void failGetByTitleOnBlankSearchTitle() {
        client.get()
                .uri("/api/lineups?title=    ")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid search title",
                        "code": "LINEUP_TITLE_BLANK",
                        "detail": "Lineup title cannot be blank",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/lineups/title-blank"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // TODO: Reconsider this validation
    @Test
    void failGetByTitleOnEmptySearchTitle() {
        client.get()
                .uri("/api/lineups?title=")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody() // TODO: fix war crime
                .json("""
                        {
                        "status": 400,
                        "title": "Bad Request",
                        "code": "REQUEST_CONSTRAINT_VIOLATION",
                        "detail": "getLineups.title: Title must be between 3 and 40 characters",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/validation/constraint-violation"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // getByID
    @Test
    void successfulGetById() {
        LineupWithAuthorDTO expectedLineup = new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT,
                "lineupOne", "bodyOne", 1L, null, null, "userOne");

        LineupWithAuthorDTO response = client.get()
                .uri("/api/lineups/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<LineupWithAuthorDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineup);
    }

    @Test
    void failToGetByIdOnNonexistentId() {
        client.get()
                .uri("/api/lineups/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 404,
                        "title": "Lineup not found",
                        "code": "LINEUP_NOT_FOUND",
                        "detail": "No lineup with id: '999' exists",
                        "instance": "/api/lineups/999",
                        "type": "https://lineup-larry.dev/problems/lineups/not-found"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulGetAllLineupsFromUser() {
        // user 2 has lineups, 2 and 3:
        List<LineupWithAuthorDTO> expectedLineups = List.of(
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"),
                new LineupWithAuthorDTO(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree",
                        2L, null, null, "userTwo"),
                new LineupWithAuthorDTO(9L, Agent.YORU, Map.HAVEN, "teleport thingy",
                        "good for post plant", 2L, null, null, "userTwo"));

        var response = client.get()
                .uri("/api/lineups/user/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineups);
    }

    @Test
    void successfulGetAllLineupsFromUserWithoutLineups() {
        // in test-data.sql user 4 and 5 have no lineups
        var response = client.get()
                .uri("/api/lineups/user/4")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response).isEmpty();
    }

    // TODO: rethink error code and the type
    @Test
    void failGetAlLineupsFromUserWithInvalidUserId() {
        client.get()
                .uri("/api/lineups/user/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 404,
                        "title": "User not found",
                        "code": "LINEUP_USER_NOT_FOUND",
                        "detail": "No user with id: '999' exists",
                        "instance": "/api/lineups/user/999",
                        "type": "https://lineup-larry.dev/problems/lineups/user-not-found"
                        }
                              """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulCreateLineup() {
        Lineup lineupToCreate = new Lineup(null, Agent.SOVA, Map.ASCENT, "lineup to create",
                "body to create", 2L, null, null);

        var response = client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<Lineup>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response.id()).isEqualTo(101);
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void successfulCreateLineupWithoutId() {
        String lineupWithoutIdJsonTemplate = """
                {"title":"lineup to create","body":"body to create","agent":"SOVA","map":"ICEBOX","userId":2}\
                """;

        var respone = client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithoutIdJsonTemplate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<Lineup>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(respone.id()).isEqualTo(101);
    }

    // TODO: reconsider this test
    @Test
    void createLineupWithUserIdAsString() {
        String lineupWithUserIdAsStringJsonTemplate = """
                {"title":"lineup to create","body":"body to create","agent":"SOVA","map":"ICEBOX","userId":"2"}\
                """;

        var response = client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithUserIdAsStringJsonTemplate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<Lineup>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response.id()).isEqualTo(101);
    }

    // TODO: fix the details random order, this is an issue on all fail, on create
    // lineup test
    @Test
    void failCreateOnNullTitle() {
        String lineupWithNullTitleJsonTemplate = """
                {"title": null,"body":"valid body","agent":"SOVA","map":"ASCENT","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithNullTitleJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                          "status": 400,
                          "title": "Invalid data",
                          "code": "REQUEST_INVALID_BODY",
                          "instance": "/api/lineups",
                          "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnNullBody() {
        String lineupWithNullBodyJsonTemplate = """
                {"title":"valid title","agent":"SOVA","map":"ASCENT","body":null,"userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithNullBodyJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnBlankTitle() {
        String lineupWithBlankTitleJsonTemplate = """
                {"title": "  ","body":"valid body","agent":"SOVA","map":"ASCENT","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithBlankTitleJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                            """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnMissingAgent() {
        String lineupWithNullAgentJsonTemplate = """
                {"title":"valid title","body":"valid body","map":"ASCENT","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithNullAgentJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void failCreateOnInvalidAgent() {
        String lineupWithInvalidAgentJsonTemplate = """
                {"title":"valid title","body":"valid body","agent":"invalidAgent","map":"ASCENT","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithInvalidAgentJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid agent",
                        "code": "LINEUP_INVALID_AGENT",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/lineups/invalid-agent"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnMissingMap() {
        String lineupWithNullMapJsonTemplate = """
                {"title":"valid title","body":"valid body","agent":"SOVA","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithNullMapJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnInvalidMap() {
        String lineupWithInvalidMapJsonTemplate = """
                {"title":"valid title","body":"valid body","agent":"SOVA","map":"invalidMap","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithInvalidMapJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid map",
                        "code": "LINEUP_INVALID_MAP",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/lineups/invalid-map"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnBlankBody() {
        String lineupWithBlankBodyJsonTemplate = """
                {"title":"valid title","agent":"SOVA","map":"ASCENT","body":"  ","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithBlankBodyJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                            """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnEmptyTitle() {
        String lineupWithEmptyTitleJsonTemplate = """
                {"title": "","agent":"SOVA","map":"ASCENT","body":"valid body","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithEmptyTitleJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failCreateOnEmptyBody() {
        String lineupWithEmptyBodyJsonTemplate = """
                {"title":"valid title","agent":"SOVA","map":"ASCENT","body":"","userId":2}\
                """;

        client.post()
                .uri("/api/lineups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithEmptyBodyJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulUpdate() {
        Lineup updatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title",
                "updated body", 1L, null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedLineup)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        LineupWithAuthorDTO response = client.get()
                .uri("/api/lineups/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<LineupWithAuthorDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("authorUsername", "createdAt", "updatedAt")
                .isEqualTo(updatedLineup);
    }

    @Test
    void failUpdateOnNullLineupId() {
        Lineup badUpdatedLineup = new Lineup(null, Agent.SOVA, Map.ASCENT, "updated title",
                "updated body", 1L, null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Lineup id's cannot be altered",
                        "code": "LINEUP_ID_MISMATCH",
                        "detail": "Cannot change lineup id from: '1' to: 'null'",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/lineups/id-mismatch"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnNullTitle() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, null, "updated body", 1L,
                null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnNullBody() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", null, 1L,
                null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnBlankTitle() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "  ", "updated body", 1L,
                null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnBlankBody() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", "  ", 1L,
                null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnEmptyTitle() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "", "updated body", 1L,
                null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnEmptyBody() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", "", 1L,
                null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid data",
                        "code": "REQUEST_INVALID_BODY",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/request/invalid-body"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnInvalidAgent() {
        String lineupWithInvalidAgentJsonTemplate = """
                {"id":1,"title":"valid title","body":"valid body","agent":"INVALIDAGENT","map":"ASCENT","userId":1}\
                """;

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithInvalidAgentJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid agent",
                        "code": "LINEUP_INVALID_AGENT",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/lineups/invalid-agent"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void failUpdateOnInvalidMap() {
        String lineupWithInvalidMapJsonTemplate = """
                {"id":1,"title":"valid title","body":"valid body","agent":"BREACH","map":"INVALIDMAP","userId":1}\
                """;

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lineupWithInvalidMapJsonTemplate)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid map",
                        "code": "LINEUP_INVALID_MAP",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/lineups/invalid-map"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // fail to update due to changed lineupId
    // ex: update lineupId 1 to a lineup with lineupId 200
    // note the user has to be owner of the lineup we're updating
    @Test
    void failUpdateOnChangedId() {
        Lineup badUpdatedLineup = new Lineup(33L, Agent.SOVA, Map.ASCENT, "updated title",
                "updated body", 1L, null, null);

        client.put()
                .uri("/api/lineups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(badUpdatedLineup)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Lineup id's cannot be altered",
                        "code": "LINEUP_ID_MISMATCH",
                        "detail": "Cannot change lineup id from: '1' to: '33'",
                        "instance": "/api/lineups/1",
                        "type": "https://lineup-larry.dev/problems/lineups/id-mismatch"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // fail to update due to userId not matching user principal (TODO: after auth)
    // ex: a lineup has userId set to 2, user 2 then tries to update the lineup's id
    // to 5

    @Test
    void successfulDelete() {
        client.delete()
                .uri("/api/lineups/2")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void deleteOnNonexistentLineup() {
        client.delete()
                .uri("/api/lineups/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 404,
                        "title": "Lineup not found",
                        "code": "LINEUP_NOT_FOUND",
                        "detail": "No lineup with id: '999' exists",
                        "instance": "/api/lineups/999",
                        "type": "https://lineup-larry.dev/problems/lineups/not-found"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    // unsuccessfully delete TODO: once auth has been impl
    void failDeleteOnInvalidAuth() {
    }

    @Test
    void getAllSovaLineups() {
        List<LineupWithAuthorDTO> response = client.get()
                .uri("/api/lineups?agent=sova")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                })
                .returnResult()
                .getResponseBody();

        List<LineupWithAuthorDTO> expectedResult = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt").isEqualTo(expectedResult);
    }

    // TODO: consider if the searchparams should be a part of the error message
    @Test
    void failGetAllLineupsOnInvalidAgent() {
        client.get()
                .uri("/api/lineups?agent=notARealAgent")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid agent",
                        "code": "LINEUP_INVALID_AGENT",
                        "detail": "The agent: 'notARealAgent' is not a valid agent",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/lineups/invalid-agent"
                        }
                        """, JsonCompareMode.LENIENT);

    }

    @Test
    void getAllSunsetLineups() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=sunset",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedResult = List.of(
                new LineupWithAuthorDTO(4L, Agent.CYPHER, Map.SUNSET, "lineupFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(13L, Agent.OMEN, Map.SUNSET, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    @Test
    void failGetAllLineupsFromAnInvalidMap() {
        client.get()
                .uri("/api/lineups?map=thisIsNotAMap")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                        "status": 400,
                        "title": "Invalid map",
                        "code": "LINEUP_INVALID_MAP",
                        "detail": "The map: 'thisIsNotAMap' is not a valid map",
                        "instance": "/api/lineups",
                        "type": "https://lineup-larry.dev/problems/lineups/invalid-map"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void getAllLineupsFromMapThatDoesNotHaveAnyLineups() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=pearl",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        assertThat(response).isEmpty();
    }

    @Test
    void getAllLineupsFromAgentWithoutLineups() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?agent=reyna",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        assertThat(response).isEmpty();
    }

    @Test
    void getAllLineupsFromAgentAndMapWithNoMatches() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=pearl&agent=jett",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        assertThat(response).isEmpty();
    }

    // TODO: get both errors, figure it out
    // in the event both agent and map are invalid the agent should err first
    @Test
    void getAllLineupsOnInvalidAgentAndMap() {
        String response = getBody("/api/lineups?agent=NotJett&map=notAscent",
                HttpStatus.BAD_REQUEST);

        assertThat(response).contains("The agent: 'NotJett' is not a valid agent");
    }

    @Test
    void getAllLineupsFromAgentMapAndTitleWithNoMatches() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=pearl&agent=jett&title=nope",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        assertThat(response).isEmpty();
    }

    @Test
    void getAllSovaLineupsOnAscent() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?agent=sova&map=ascent",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedLineups = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineups);
    }

    @Test
    void getAllAscentMapsWithSpecificTitle() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=ascent&title=lineupTwo",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedResult = List.of(new LineupWithAuthorDTO(2L, Agent.SOVA,
                Map.ASCENT, "lineupTwo", "bodyTwo", 2L, null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    @Test
    void getAllSovaLineupsWithSpecificTitle() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?agent=sova&title=lineupTwo",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(new LineupWithAuthorDTO(2L, Agent.SOVA,
                Map.ASCENT, "lineupTwo", "bodyTwo", 2L, null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // TODO: are all of these test really needed?
    @Test
    void getAllLineupByAgentMapAndTitle() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?agent=brimstone&map=bind&title=lineupThree",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedLineup = List.of(new LineupWithAuthorDTO(3L,
                Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree", 2L, null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineup);
    }

    @Test
    void getAllLineupByAgentMapAndTitleInvalidAgent() {
        String response = getBody("/api/lineups?agent=brimmystonero&map=bind&title=lineupThree",
                HttpStatus.BAD_REQUEST);

        assertThat(response).contains("The agent: 'brimmystonero' is not a valid agent");
    }

    @Test
    void getAllLineupByAgentMapAndTitleInvalidMap() {
        String response = getBody(
                "/api/lineups?agent=brimstone&map=bindersToBeBinding&title=lineupThree",
                HttpStatus.BAD_REQUEST);

        assertThat(response).contains("The map: 'bindersToBeBinding' is not a valid map");
    }

    // getByTitlePagination
    // expect to get just the first lineup
    @Test
    void successfulGetByTitlePaginationSmallPageSize() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?title=same+name&pageSize=1",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedResult = List.of(new LineupWithAuthorDTO(5L,
                Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L, null, null,
                "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    // expect to get exactly the 2 same name lineups
    // TODO: standardize these betters
    @Test
    void successfulGetByTitlePagination() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?title=same+name&pageSize=3",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedResult = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    // expect to get the last same name lineup
    @Test
    void successfulGetByTitlePaginationSeek() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?title=same+name&pageSize=1&lastValue=5",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedResult = List.of(new LineupWithAuthorDTO(6L,
                Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L, null, null,
                "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    // findByAgentPaginated
    @Test
    void successfulFindByAgentPagination() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?agent=sova",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByAgentPaginationSeek() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?agent=sova&pageSize=1&lastValue=1",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(new LineupWithAuthorDTO(2L, Agent.SOVA,
                Map.ASCENT, "lineupTwo", "bodyTwo", 2L, null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // findByMapPaginated
    @Test
    void successfulFindByMapPagination() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=ascent",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"),
                new LineupWithAuthorDTO(18L, Agent.KAYO, Map.ASCENT, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByMapPaginationSeek() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=ascent&lastValue=1",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"),
                new LineupWithAuthorDTO(18L, Agent.KAYO, Map.ASCENT, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // findByAgentAndMapPaginated
    @Test
    void successfulFindByAgentAntMapPagination() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=ascent",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"),
                new LineupWithAuthorDTO(18L, Agent.KAYO, Map.ASCENT, "titleFour", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByAgentAndMapPaginationSeek() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?map=ascent&agent=sova&lastValue=1",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(new LineupWithAuthorDTO(2L, Agent.SOVA,
                Map.ASCENT, "lineupTwo", "bodyTwo", 2L, null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // findByMapAndTitlePaginated
    @Test
    void successfulFindByMapAndTitlePaginated() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?map=icebox&title=same+name",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByMapAndTitlePaginatedSeek() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?map=icebox&title=same+name&lastValue=5",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(new LineupWithAuthorDTO(6L, Agent.KILLJOY,
                Map.ICEBOX, "same name", "bodyFour", 3L, null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // findByAgentAndTitlePaginated
    @Test
    void findByAgentAndTitlePaginated() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?agent=killjoy&title=same+name",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void findByAgentAndTitlePaginatedSeek() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?agent=killjoy&title=same+name&lastValue=5",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(new LineupWithAuthorDTO(6L, Agent.KILLJOY,
                Map.ICEBOX, "same name", "bodyFour", 3L, null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // findByAgentAndMapAndTitlePaginated
    @Test
    void findByAgentAndMapAndTitlePaginated() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?agent=killjoy&map=icebox&title=same+name",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedLineup = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineup);
    }

    @Test
    void findByAgentAndMapAndTitlePaginatedSeek() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?agent=killjoy&map=icebox&title=same+name&lastValue=5",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedLineup = List.of(new LineupWithAuthorDTO(6L,
                Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L, null, null,
                "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineup);
    }

    // getAllLineupsPaginated
    @Test
    void getAllLineupsPaginated() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?pageSize=2",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L,
                        null, null, "userOne"),
                new LineupWithAuthorDTO(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L,
                        null, null, "userTwo"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void getAllLineupsPaginatedSeek() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups?pageSize=2&lastValue=2",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree",
                        2L, null, null, "userTwo"),
                new LineupWithAuthorDTO(4L, Agent.CYPHER, Map.SUNSET, "lineupFour", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    // getByAuthor
    @Test
    void getAllLineupsFromAuthorPageSized() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups/user/3?pageSize=2",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(4L, Agent.CYPHER, Map.SUNSET, "lineupFour", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void getAllLineupsFromAuthorPageSizedSeek() {
        List<LineupWithAuthorDTO> response = getOkBody("/api/lineups/user/3?pageSize=2&lastValue=4",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });

        List<LineupWithAuthorDTO> expectedList = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedList);
    }

    @Test
    void GetAllLineupsFromNonexistentUser() {
        String response = getBody("/api/lineups/user/999?pageSize=10", HttpStatus.NOT_FOUND);

        assertThat(response).isNotEmpty();
        assertThat(response).contains("No user with id: '999' exists");
    }

    @Test
    void getAllLineupsFromNonexistentUserPaginated() {
        String response = getBody("/api/lineups/user/999?lastValue=72", HttpStatus.NOT_FOUND);

        assertThat(response).isNotEmpty();
        assertThat(response).contains("No user with id: '999' exists");
    }

    // seeking on "bad" values, ie does not exist or the seek value does not fit the
    // query criteria
    @Test
    void failPaginationOnNonexistentLineup() {
        List<LineupWithAuthorDTO> response = getOkBody(
                "/api/lineups?map=pearl&agent=vyse&lastValue=999",
                new ParameterizedTypeReference<List<LineupWithAuthorDTO>>() {
                });
        // JOOQ does not throw an error, it just returns an empty list, which we'll deem
        // as fine and
        // return it as well

        assertThat(response).isEmpty();
    }
}
