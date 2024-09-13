package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureMockMvc
public class LineupIntegrationTest {

    // opting for TestRestTemplate for now, might swap to RestClient once stable
    @Autowired
    private TestRestTemplate restTemplate;

    private static HttpHeaders headers;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.3-alpine"
    );

    @BeforeAll
    static void initHeader() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void successfulGetAll() {
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Lineup> expectedArray = List.of(
                new Lineup(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L),
                new Lineup(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L),
                new Lineup(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree", 2L),
                new Lineup(4L, Agent.CYPHER, Map.SUNSET, "lineupFour", "bodyFour", 3L),
                new Lineup(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L),
                new Lineup(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L)
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(expectedArray.size());
        assertThat(res.getBody().stream().toList()).isEqualTo(expectedArray);
    }

    @Test
    void successfulSearchByTitleWithMatches() {
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups?title=same+name",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Lineup> expectedArray = List.of(
                new Lineup(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L),
                new Lineup(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L)
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(2);
        assertThat(res.getBody().stream().toList()).isEqualTo(expectedArray);
    }

    @Test
    void successfulSearchByTitleWithNoMatches() {
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups?title=this+will+not+give+any+results",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(0);
    }

    @Test
    void failGetByTitleOnBlankSearchTitle() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/lineups?title=    ",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Search title cannot be blank");
        assertThat(response.getBody()).contains("Cannot search for string: '    ', since it's blank");
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
        assertThat(response.getBody()).contains("Search title cannot be empty");
        assertThat(response.getBody()).contains("Cannot search for string: '', since it's empty");
    }

    // getByID
    @Test
    void successfulGetById() {
        ResponseEntity<Optional<Lineup>> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Lineup expectedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "lineupOne", "bodyOne", 1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).isPresent();
        assertThat(res.getBody().get()).isEqualTo(expectedLineup);
    }

    @Test
    void failToGetByIdOnNonexistentId() {
        ResponseEntity<Optional<Lineup>> res = restTemplate.exchange(
                "/api/lineups/999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void successfulGetAllLineupsFromUser() {
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups/user/2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // user 2 has lineups, 2 and 3:
        List<Lineup> expectedLineups = List.of(
                new Lineup(2L, Agent.SOVA, Map.ASCENT, "lineupTwo", "bodyTwo", 2L),
                new Lineup(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree", 2L)
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(2);
        assertThat(res.getBody().stream().toList()).isEqualTo(expectedLineups);
    }

    @Test
    void successfulGetAllLineupsFromUserWithoutLineups() {
        // in test-data.sql user 4 and 5 have no lineups
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups/user/4",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEmpty();
    }

    @Test
    void failGetALlLineupsFromUserWithInvalidUserId() throws Exception {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/user/999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("User not found");
        assertThat(res.getBody()).contains("No user with id: '999' exists");
    }

    @Test
    void successfulCreateLineup() {
        Lineup lineupToCreate = new Lineup(null, Agent.SOVA, Map.ASCENT, "lineup to create", "body to create", 2L);

        ResponseEntity<Lineup> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>(lineupToCreate, headers),
                Lineup.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().id()).isNotNull();
        // sequence has been set to start of at 101, to give headroom for seed data, only applies to "dev"-env
        assertThat(res.getBody().id()).isEqualTo(101);
    }


    @Test
    void successfulCreateLineupWithoutId() {
        String lineupWithoutIdJsonTemplate = """
                {"title":"lineup to create","body":"body to create","agent":"SOVA","map":"ICEBOX","userId":2}""";

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
    void failCreateOnNullTitle() {
        String lineupWithNullTitleJsonTemplate = """
                {"title": null,"body":"valid body","agent":"SOVA","map":"ASCENT","userId":2}""";

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
                {"title":"valid title","agent":"SOVA","map":"ASCENT","body":null,"userId":2}""";

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
                {"title": "  ","body":"valid body","agent":"SOVA","map":"ASCENT","userId":2}""";

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
                {"title":"valid title","body":"valid body","map":"ASCENT","userId":2}""";

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
                {"title":"valid title","body":"valid body","agent":"invalidAgent","map":"ASCENT","userId":2}""";

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>(lineupWithInvalidAgentJsonTemplate, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid agent");
        assertThat(res.getBody()).contains("The agent 'invalidAgent' is not a valid agent");
    }

    @Test
    void failCreateOnMissingMap() {
        String lineupWithNullMapJsonTemplate = """
                {"title":"valid title","body":"valid body","agent":"SOVA","userId":2}""";


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
                {"title":"valid title","body":"valid body","agent":"SOVA","map":"invalidMap","userId":2}""";

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>(lineupWithInvalidMapJsonTemplate, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid map");
        assertThat(res.getBody()).contains("The map 'invalidMap' is not a valid map");
    }

    @Test
    void failCreateOnBlankBody() {
        String lineupWithBlankBodyJsonTemplate = """
                {"title":"valid title","agent":"SOVA","map":"ASCENT","body":"  ","userId":2}""";

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
                {"title": "","agent":"SOVA","map":"ASCENT","body":"valid body","userId":2}""";

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
                {"title":"valid title","agent":"SOVA","map":"ASCENT","body":"","userId":2}""";

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
        Lineup updatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNull();

        ResponseEntity<Optional<Lineup>> res2 = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Optional<Lineup>>() {
                }
        );

        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res2.getBody().get()).isEqualTo(updatedLineup);
    }

    @Test
    void failUpdateOnNullLineupId() {
        Lineup badUpdatedLineup = new Lineup(null, Agent.SOVA, Map.ASCENT, "updated title", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Lineup id's cannot be altered");
        assertThat(res.getBody()).contains("Cannot change lineup id from '1' to 'null'");
    }

    @Test
    void failUpdateOnNullTitle() {
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, null, "updated body", 1L);

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
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", null, 1L);

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
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "  ", "updated body", 1L);

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
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", "  ", 1L);

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
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "", "updated body", 1L);

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
        Lineup badUpdatedLineup = new Lineup(1L, Agent.SOVA, Map.ASCENT, "updated title", "", 1L);

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
                {"id":1,"title":"valid title","body":"valid body","agent":"INVALIDAGENT","map":"ASCENT","userId":1}""";

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(lineupWithInvalidAgentJsonTemplate, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid agent");
        assertThat(res.getBody()).contains("The agent 'INVALIDAGENT' is not a valid agent");
    }

    @Test
    void failUpdateOnInvalidMap() {
        String lineupWithInvalidMapJsonTemplate = """
                {"id":1,"title":"valid title","body":"valid body","agent":"BREACH","map":"INVALIDMAP","userId":1}""";

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(lineupWithInvalidMapJsonTemplate, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid map");
        assertThat(res.getBody()).contains("The map 'INVALIDMAP' is not a valid map");
    }

    // fail to update due to changed lineupId
    // ex: update lineupId 1 to a lineup with lineupId 200
    // note the user has to be owner of the lineup we're updating
    @Test
    void failUpdateOnChangedId() {
        Lineup badUpdatedLineup = new Lineup(33L, Agent.SOVA, Map.ASCENT, "updated title", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Lineup id's cannot be altered");
        assertThat(res.getBody()).contains("Cannot change lineup id from '1' to '33'");
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

    // unsuccessfully delete TODO: once auth has been impl
    void failDeleteOnInvalidAuth() {
    }


    // CURRENT: check that we serialize the maps and agents correctly


    // getByAuthor
    // byAgent
    // byMap
    // combination of all of the above "filters"
}
