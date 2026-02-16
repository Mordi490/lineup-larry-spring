package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import static org.assertj.core.api.Assertions.assertThat;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// TODO: Swap TestRestTemplate for RestTestClient
// https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide#using-webclient-or-testresttemplate-and-springboottest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestRestTemplate
public class LineupIntegrationTest {

    // opting for TestRestTemplate for now, might swap to RestClient once stable
    @Autowired
    private TestRestTemplate restTemplate;

    private static HttpHeaders headers;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
        "postgres:18-alpine"
    );

    @BeforeAll
    static void initHeader() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void successfulGetAll() {
        ResponseEntity<List<LineupWithAuthorDTO>> res = restTemplate.exchange(
            "/api/lineups",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        List<LineupWithAuthorDTO> expectedArray = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                3L,
                Agent.BRIMSTONE,
                Map.BIND,
                "lineupThree",
                "bodyThree",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                4L,
                Agent.CYPHER,
                Map.SUNSET,
                "lineupFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                7L,
                Agent.CHAMBER,
                Map.SPLIT,
                "awp crutch",
                "filler text here",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                8L,
                Agent.BREACH,
                Map.FRACTURE,
                "some flash",
                "even more filler text here",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                9L,
                Agent.YORU,
                Map.HAVEN,
                "teleport thingy",
                "good for post plant",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                10L,
                Agent.PHOENIX,
                Map.LOTUS,
                "cheeky flash",
                "then click heads",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                11L,
                Agent.SKYE,
                Map.SPLIT,
                "sick pop flash",
                "then dog",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                12L,
                Agent.VYSE,
                Map.BREEZE,
                "click heads",
                "just click the head",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                13L,
                Agent.OMEN,
                Map.SUNSET,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                14L,
                Agent.VIPER,
                Map.SPLIT,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                15L,
                Agent.SAGE,
                Map.ICEBOX,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                16L,
                Agent.RAZE,
                Map.BREEZE,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                17L,
                Agent.ASTRA,
                Map.ICEBOX,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                18L,
                Agent.KAYO,
                Map.ASCENT,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                19L,
                Agent.NEON,
                Map.FRACTURE,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                20L,
                Agent.FADE,
                Map.LOTUS,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(expectedArray.size());
        assertThat(res.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedArray);
    }

    @Test
    void successfulSearchByTitleWithMatches() {
        ResponseEntity<List<LineupWithAuthorDTO>> res = restTemplate.exchange(
            "/api/lineups?title=same+name",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        List<LineupWithAuthorDTO> expectedArray = List.of(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(2);
        assertThat(res.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedArray);
    }

    // Reconsider if this is even a good practice
    @Test
    void failGetByTitleOnBlankSearchTitle() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?title=    ",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid search title");
        assertThat(response.getBody()).contains("Lineup title cannot be blank");
    }

    @Test
    void failGetByTitleOnEmptySearchTitle() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?title=",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("REQUEST_CONSTRAINT_VIOLATION");
        assertThat(response.getBody()).contains(
            "Title must be between 3 and 40 characters"
        );
    }

    // getByID
    @Test
    void successfulGetById() {
        ResponseEntity<Optional<LineupWithAuthorDTO>> res =
            restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        LineupWithAuthorDTO expectedLineup = new LineupWithAuthorDTO(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "lineupOne",
            "bodyOne",
            1L,
            null,
            null,
            "userOne"
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).isPresent();
        assertThat(res.getBody().get())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedLineup);
    }

    @Test
    void failToGetByIdOnNonexistentId() {
        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/999",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("Lineup not found");
        assertThat(res.getBody()).contains("LINEUP_NOT_FOUND");
    }

    @Test
    void successfulGetAllLineupsFromUser() {
        ResponseEntity<List<LineupWithAuthorDTO>> res = restTemplate.exchange(
            "/api/lineups/user/2",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        // user 2 has lineups, 2 and 3:
        List<LineupWithAuthorDTO> expectedLineups = List.of(
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                3L,
                Agent.BRIMSTONE,
                Map.BIND,
                "lineupThree",
                "bodyThree",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                9L,
                Agent.YORU,
                Map.HAVEN,
                "teleport thingy",
                "good for post plant",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(expectedLineups.size());
        assertThat(res.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedLineups);
    }

    @Test
    void successfulGetAllLineupsFromUserWithoutLineups() {
        // in test-data.sql user 4 and 5 have no lineups
        ResponseEntity<List<LineupWithAuthorDTO>> res = restTemplate.exchange(
            "/api/lineups/user/4",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEmpty();
    }

    @Test
    void failGetAlLineupsFromUserWithInvalidUserId() {
        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/user/999",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("User not found");
        assertThat(res.getBody()).contains("No user with id: '999' exists");
    }

    @Test
    void successfulCreateLineup() {
        Lineup lineupToCreate = new Lineup(
            null,
            Agent.SOVA,
            Map.ASCENT,
            "lineup to create",
            "body to create",
            2L,
            null,
            null
        );

        ResponseEntity<Lineup> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupToCreate, headers),
            Lineup.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().id()).isNotNull();
        // sequence has been set to start of at 101, to give headroom for seed data, only applies to
        // "dev"-env
        assertThat(res.getBody().id()).isEqualTo(101);
        assertThat(res.getBody().createdAt()).isNotNull();
        assertThat(res.getBody().updatedAt()).isNotNull();
    }

    @Test
    void successfulCreateLineupWithoutId() {
        String lineupWithoutIdJsonTemplate = """
            {"title":"lineup to create","body":"body to create","agent":"SOVA","map":"ICEBOX","userId":2}\
            """;

        ResponseEntity<Lineup> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithoutIdJsonTemplate, headers),
            Lineup.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().id()).isEqualTo(101);
    }

    @Test
    void createLineupWithUserIdAsString() {
        String lineupWithUserIdAsStringJsonTemplate = """
            {"title":"lineup to create","body":"body to create","agent":"SOVA","map":"ICEBOX","userId":"2"}\
            """;

        ResponseEntity<Lineup> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithUserIdAsStringJsonTemplate, headers),
            Lineup.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().id()).isEqualTo(101);
    }

    @Test
    void failCreateOnNullTitle() {
        String lineupWithNullTitleJsonTemplate = """
            {"title": null,"body":"valid body","agent":"SOVA","map":"ASCENT","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithNullTitleJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("title: title cannot be blank");
    }

    @Test
    void failCreateOnNullBody() {
        String lineupWithNullBodyJsonTemplate = """
            {"title":"valid title","agent":"SOVA","map":"ASCENT","body":null,"userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithNullBodyJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("body: body cannot be blank");
    }

    @Test
    void failCreateOnBlankTitle() {
        String lineupWithBlankTitleJsonTemplate = """
            {"title": "  ","body":"valid body","agent":"SOVA","map":"ASCENT","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithBlankTitleJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("title: title cannot be blank");
    }

    @Test
    void failCreateOnMissingAgent() {
        String lineupWithNullAgentJsonTemplate = """
            {"title":"valid title","body":"valid body","map":"ASCENT","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithNullAgentJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("agent: agent cannot be null");
    }

    @Test
    void failCreateOnInvalidAgent() {
        String lineupWithInvalidAgentJsonTemplate = """
            {"title":"valid title","body":"valid body","agent":"invalidAgent","map":"ASCENT","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithInvalidAgentJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid agent");
        assertThat(res.getBody()).contains(
            "The agent: 'invalidAgent' is not a valid agent"
        );
    }

    @Test
    void failCreateOnMissingMap() {
        String lineupWithNullMapJsonTemplate = """
            {"title":"valid title","body":"valid body","agent":"SOVA","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithNullMapJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("map: map cannot be null");
    }

    @Test
    void failCreateOnInvalidMap() {
        String lineupWithInvalidMapJsonTemplate = """
            {"title":"valid title","body":"valid body","agent":"SOVA","map":"invalidMap","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithInvalidMapJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid map");
        assertThat(res.getBody()).contains(
            "The map: 'invalidMap' is not a valid map"
        );
    }

    @Test
    void failCreateOnBlankBody() {
        String lineupWithBlankBodyJsonTemplate = """
            {"title":"valid title","agent":"SOVA","map":"ASCENT","body":"  ","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithBlankBodyJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("body: body cannot be blank");
    }

    @Test
    void failCreateOnEmptyTitle() {
        String lineupWithEmptyTitleJsonTemplate = """
            {"title": "","agent":"SOVA","map":"ASCENT","body":"valid body","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithEmptyTitleJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("title: title cannot be empty");
    }

    @Test
    void failCreateOnEmptyBody() {
        String lineupWithEmptyBodyJsonTemplate = """
            {"title":"valid title","agent":"SOVA","map":"ASCENT","body":"","userId":2}\
            """;

        ResponseEntity<String> res = restTemplate.postForEntity(
            "/api/lineups",
            new HttpEntity<>(lineupWithEmptyBodyJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("body: body cannot be empty");
    }

    @Test
    void successfulUpdate() {
        Lineup updatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "updated title",
            "updated body",
            1L,
            null,
            null
        );

        ResponseEntity<Optional<LineupWithAuthorDTO>> res =
            restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedLineup, headers),
                new ParameterizedTypeReference<>() {}
            );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNull();

        ResponseEntity<Optional<LineupWithAuthorDTO>> res2 =
            restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res2.getBody()).isPresent();
        assertThat(res2.getBody().get())
            .usingRecursiveComparison()
            .ignoringFields("authorUsername", "createdAt", "updatedAt")
            .isEqualTo(updatedLineup);
    }

    @Test
    void failUpdateOnNullLineupId() {
        Lineup badUpdatedLineup = new Lineup(
            null,
            Agent.SOVA,
            Map.ASCENT,
            "updated title",
            "updated body",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Lineup id's cannot be altered");
        assertThat(res.getBody()).contains(
            "Cannot change lineup id from: '1' to: 'null'"
        );
    }

    @Test
    void failUpdateOnNullTitle() {
        Lineup badUpdatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            null,
            "updated body",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("title: title cannot be null");
    }

    @Test
    void failUpdateOnNullBody() {
        Lineup badUpdatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "updated title",
            null,
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("body: body cannot be null");
    }

    @Test
    void failUpdateOnBlankTitle() {
        Lineup badUpdatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "  ",
            "updated body",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("title: title cannot be blank");
    }

    @Test
    void failUpdateOnBlankBody() {
        Lineup badUpdatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "updated title",
            "  ",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("body: body cannot be blank");
    }

    @Test
    void failUpdateOnEmptyTitle() {
        Lineup badUpdatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "",
            "updated body",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("title: title cannot be empty");
    }

    @Test
    void failUpdateOnEmptyBody() {
        Lineup badUpdatedLineup = new Lineup(
            1L,
            Agent.SOVA,
            Map.ASCENT,
            "updated title",
            "",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("body: body cannot be empty");
    }

    @Test
    void failUpdateOnInvalidAgent() {
        String lineupWithInvalidAgentJsonTemplate = """
            {"id":1,"title":"valid title","body":"valid body","agent":"INVALIDAGENT","map":"ASCENT","userId":1}\
            """;

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(lineupWithInvalidAgentJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid agent");
        assertThat(res.getBody()).contains(
            "The agent: 'INVALIDAGENT' is not a valid agent"
        );
    }

    @Test
    void failUpdateOnInvalidMap() {
        String lineupWithInvalidMapJsonTemplate = """
            {"id":1,"title":"valid title","body":"valid body","agent":"BREACH","map":"INVALIDMAP","userId":1}\
            """;

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(lineupWithInvalidMapJsonTemplate, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid map");
        assertThat(res.getBody()).contains(
            "The map: 'INVALIDMAP' is not a valid map"
        );
    }

    // fail to update due to changed lineupId
    // ex: update lineupId 1 to a lineup with lineupId 200
    // note the user has to be owner of the lineup we're updating
    @Test
    void failUpdateOnChangedId() {
        Lineup badUpdatedLineup = new Lineup(
            33L,
            Agent.SOVA,
            Map.ASCENT,
            "updated title",
            "updated body",
            1L,
            null,
            null
        );

        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/1",
            HttpMethod.PUT,
            new HttpEntity<>(badUpdatedLineup, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Lineup id's cannot be altered");
        assertThat(res.getBody()).contains(
            "Cannot change lineup id from: '1' to: '33'"
        );
    }

    // fail to update due to userId not matching user principal (TODO: after auth)
    // ex: a lineup has userId set to 2, user 2 then tries to update the lineup's id to 5

    @Test
    void successfulDelete() {
        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/2",
            HttpMethod.DELETE,
            new HttpEntity<>(null, headers),
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void deleteOnNonexistentLineup() {
        ResponseEntity<String> res = restTemplate.exchange(
            "/api/lineups/999",
            HttpMethod.DELETE,
            null,
            String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).contains("No lineup with id: '999' exists");
    }

    // unsuccessfully delete TODO: once auth has been impl
    void failDeleteOnInvalidAuth() {}

    @Test
    void getAllSovaLineups() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=sova",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedResult = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResult);
    }

    @Test
    void failGetAllLineupsOnInvalidAgent() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?agent=notARealAgent",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(
            "The agent: 'notARealAgent' is not a valid agent"
        );
    }

    @Test
    void getAllSunsetLineups() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=sunset",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedResult = List.of(
            new LineupWithAuthorDTO(
                4L,
                Agent.CYPHER,
                Map.SUNSET,
                "lineupFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                13L,
                Agent.OMEN,
                Map.SUNSET,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResult);
    }

    @Test
    void failGetAllLineupsFromAnInvalidMap() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?map=thisIsNotAMap",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(
            "The map: 'thisIsNotAMap' is not a valid map"
        );
    }

    @Test
    void getAllLineupsFromMapThatDoesNotHaveAnyLineups() {
        // 10/10 naming yerp
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?map=pearl",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(
            """
            []\
            """
        ); // empty list
    }

    @Test
    void getAllLineupsFromAgentWithoutLineups() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?agent=reyna",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(
            """
            []\
            """
        ); // empty list
    }

    @Test
    void getAllLineupsFromAgentAndMapWithNoMatches() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?map=pearl&agent=jett",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(
            """
            []\
            """
        ); // empty list
    }

    // in the event both agent and map are invalid the agent should err first
    @Test
    void getAllLineupsOnInvalidAgentAndMap() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?agent=NotJett&map=notAscent",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(
            "The agent: 'NotJett' is not a valid agent"
        );
    }

    @Test
    void getAllLineupsFromAgentMapAndTitleWithNoMatches() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=pearl&agent=jett&title=nope",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().stream().toList()).isEqualTo(
            Collections.EMPTY_LIST
        );
    }

    @Test
    void getAllSovaLineupsOnAscent() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=sova&map=ascent",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedLineups = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedLineups);
    }

    @Test
    void getAllAscentMapsWithSpecificTitle() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=ascent&title=lineupTwo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedResult = List.of(
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResult);
    }

    @Test
    void getAllSovaLineupsWithSpecificTitle() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=sova&title=lineupTwo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = Collections.singletonList(
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void getAllLineupByAgentMapAndTitle() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=brimstone&map=bind&title=lineupThree",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedLineup = List.of(
            new LineupWithAuthorDTO(
                3L,
                Agent.BRIMSTONE,
                Map.BIND,
                "lineupThree",
                "bodyThree",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().stream().toList())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedLineup);
    }

    @Test
    void getAllLineupByAgentMapAndTitleInvalidAgent() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?agent=brimmystonero&map=bind&title=lineupThree",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(
            "The agent: 'brimmystonero' is not a valid agent"
        );
    }

    @Test
    void getAllLineupByAgentMapAndTitleInvalidMap() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups?agent=brimstone&map=bindersToBeBinding&title=lineupThree",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains(
            "The map: 'bindersToBeBinding' is not a valid map"
        );
    }

    // getByTitlePagination
    // expect to get just the first lineup
    @Test
    void successfulGetByTitlePaginationSmallPageSize() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?title=same+name&pageSize=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedResult = Collections.singletonList(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResult);
    }

    // expect to get exactly the 2 same name lineups
    // TODO: standardize these betters
    @Test
    void successfulGetByTitlePagination() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?title=same+name&pageSize=3",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedResult = List.of(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResult);
    }

    // expect to get the last same name lineup
    @Test
    void successfulGetByTitlePaginationSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?title=same+name&pageSize=1&lastValue=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedResult = Collections.singletonList(
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResult);
    }

    // findByAgentPaginated
    @Test
    void successfulFindByAgentPagination() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=sova",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByAgentPaginationSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=sova&pageSize=1&lastValue=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    // findByMapPaginated
    @Test
    void successfulFindByMapPagination() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=ascent",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                18L,
                Agent.KAYO,
                Map.ASCENT,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByMapPaginationSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=ascent&lastValue=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                18L,
                Agent.KAYO,
                Map.ASCENT,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    // findByAgentAndMapPaginated
    @Test
    void successfulFindByAgentAntMapPagination() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=ascent",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                18L,
                Agent.KAYO,
                Map.ASCENT,
                "titleFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByAgentAndMapPaginationSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=ascent&agent=sova&lastValue=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    // findByMapAndTitlePaginated
    @Test
    void successfulFindByMapAndTitlePaginated() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=icebox&title=same+name",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void successfulFindByMapAndTitlePaginatedSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=icebox&title=same+name&lastValue=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    // findByAgentAndTitlePaginated
    @Test
    void findByAgentAndTitlePaginated() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=killjoy&title=same+name",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void findByAgentAndTitlePaginatedSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=killjoy&title=same+name&lastValue=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    // findByAgentAndMapAndTitlePaginated
    @Test
    void findByAgentAndMapAndTitlePaginated() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?agent=killjoy&map=icebox&title=same+name",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedLineup = List.of(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedLineup);
    }

    @Test
    void findByAgentAndMapAndTitlePaginatedSeek() {
        ResponseEntity<List<Lineup>> response = restTemplate.exchange(
            "/api/lineups?agent=killjoy&map=icebox&title=same+name&lastValue=5",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Lineup>>() {}
        );

        List<Lineup> expectedLineup = List.of(
            new Lineup(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedLineup);
    }

    // getAllLineupsPaginated
    @Test
    void getAllLineupsPaginated() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?pageSize=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                1L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupOne",
                "bodyOne",
                1L,
                null,
                null,
                "userOne"
            ),
            new LineupWithAuthorDTO(
                2L,
                Agent.SOVA,
                Map.ASCENT,
                "lineupTwo",
                "bodyTwo",
                2L,
                null,
                null,
                "userTwo"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void getAllLineupsPaginatedSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?pageSize=2&lastValue=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                3L,
                Agent.BRIMSTONE,
                Map.BIND,
                "lineupThree",
                "bodyThree",
                2L,
                null,
                null,
                "userTwo"
            ),
            new LineupWithAuthorDTO(
                4L,
                Agent.CYPHER,
                Map.SUNSET,
                "lineupFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    // getByAuthor
    @Test
    void getAllLineupsFromAuthorPageSized() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups/user/3?pageSize=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                4L,
                Agent.CYPHER,
                Map.SUNSET,
                "lineupFour",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void getAllLineupsFromAuthorPageSizedSeek() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups/user/3?pageSize=2&lastValue=4",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

        List<LineupWithAuthorDTO> expectedList = List.of(
            new LineupWithAuthorDTO(
                5L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            ),
            new LineupWithAuthorDTO(
                6L,
                Agent.KILLJOY,
                Map.ICEBOX,
                "same name",
                "bodyFour",
                3L,
                null,
                null,
                "userThree"
            )
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody())
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedList);
    }

    @Test
    void GetAllLineupsFromNonexistentUser() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups/user/999?pageSize=10",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).contains(
            "No user with id: '999' exists"
        );
    }

    @Test
    void getAllLineupsFromNonexistentUserPaginated() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/lineups/user/999?lastValue=72",
            HttpMethod.GET,
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).contains(
            "No user with id: '999' exists"
        );
    }

    // seeking on "bad" values, ie does not exist or the seek value does not fit the query criteria
    @Test
    void failPaginationOnNonexistentLineup() {
        ResponseEntity<List<LineupWithAuthorDTO>> response =
            restTemplate.exchange(
                "/api/lineups?map=pearl&agent=vyse&lastValue=999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );
        // JOOQ does not throw an error, it just returns an empty list, which we'll deem as fine and
        // return it as well

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().stream().toList()).isEmpty();
    }
}
