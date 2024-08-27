package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
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

    // temp sol on certain tests, till RestClient becomes stable
    @Autowired
    MockMvc mockMvc;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.3-alpine"
    );

    // getAll
    @Test
    void successfulGetAll() {
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(6);
        assertThat(res.getBody().getFirst()).isInstanceOf(Lineup.class);
        assertThat(res.getBody().getFirst()).isEqualTo(new Lineup(1L, "lineupOne", "bodyOne", 1L));
    }

    // getByID
    @Test
    void getFromValidId() {
        ResponseEntity<Optional<Lineup>> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody()).isPresent();
        assertThat(res.getBody().get()).isEqualTo(new Lineup(1L, "lineupOne", "bodyOne", 1L));
    }

    @Test
    void getFromNonexistentId() {
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

    // getAllLineupsFromAUser
    // get from an existing user with lineups
    @Test
    void successfulGetAllLineupsFromUser() {
        ResponseEntity<List<Lineup>> res = restTemplate.exchange(
                "/api/lineups/user/2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(2);
        assertThat(res.getBody().getFirst()).isInstanceOf(Lineup.class);
        assertThat(res.getBody().getFirst()).isEqualTo(new Lineup(2L, "lineupTwo", "bodyTwo", 2L));
    }

    // get from an existing user without any lineups
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

    // get from a nonexistent user
    @Test
    void failGetALlLineupsFromUserWithInvalidUserId() throws Exception {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/user/999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getBody().toString()).contains("No user with id: '999' exists");
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        // we expect to receive {detail: no user with id: '999' exists, instance: /api/users/999 ...}
        assertThat(res.toString()).contains("User not found");
    }

    // create
    // valid create data
    @Test
    void successfulCreateLineup() {
        Lineup lineupToCreate = new Lineup(null, "lineup to create", "body to create", 2L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Lineup> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>(lineupToCreate, headers),
                Lineup.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().id()).isNotNull();
        // sequence has been set to start of at 101, to give headroom for seed data
        assertThat(res.getBody().id()).isEqualTo(101);
    }

    @Test
    void successfulCreateLineupWithoutId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Lineup> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title":"lineup to create","body":"body to create","userId":2}""", headers),
                Lineup.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().id()).isNotNull();
        // sequence has been set to start of at 101, to give headroom for seed data
        assertThat(res.getBody().id()).isEqualTo(101);
    }

    // various invalid data cases; blank, empty null, etc.
    @Test
    void failOnNullTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title": null,"body":"valid body","userId":2}""", headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).containsIgnoringCase("title cannot be null");
    }

    @Test
    void failOnNullBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title": "valid title","body":null,"userId":2}""", headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).containsIgnoringCase("body cannot be null");
    }

    @Test
    void failOnBlankTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title": "  ","body":"valid body","userId":2}""", headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).containsIgnoringCase("title cannot be blank");
    }

    @Test
    void failOnBlankBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title": "valid title","body":"  ","userId":2}""", headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).containsIgnoringCase("body cannot be blank");
    }

    @Test
    void failOnEmptyTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title": "","body":"valid body","userId":2}""", headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).containsIgnoringCase("title cannot be empty");
    }

    @Test
    void failOnEmptyBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> res = restTemplate.postForEntity(
                "/api/lineups",
                new HttpEntity<>("""
                        {"title": "valid title","body":"","userId":2}""", headers),
                Void.class
        );

        System.out.println("evidence: " + res.toString());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // assertThat(res.toString()).containsIgnoringCase("body cannot be blank");
    }

    // update
    // valid update
    @Test
    void successfulUpdate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup updatedLineup = new Lineup(1L, "updated title", "updated body", 1L);

        ResponseEntity<Void> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedLineup, headers),
                Void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNull();
    }

    // invalid update due to bad data; blank, empty null, etc
    // NB! this end up triggering a "lineup id cannot be altered"
    // TODO: confirm passes
    @Test
    void failToUpdateNullLineupId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(null, "updated title", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).contains("Cannot change lineup id from '1' to 'null'");
    }

    @Test
    void failToUpdateNullTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(1L, null, "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).contains("cannot be null");
    }

    @Test
    void failToUpdateNullBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(1L, "updated title", null, 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).contains("cannot be null");
    }

    @Test
    void failToUpdateBlankTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(1L, "  ", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).contains("title cannot be blank");
    }

    @Test
    void failToUpdateBlankBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(1L, "updated title", "  ", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).contains("body cannot be blank");
    }

    @Test
    void failToUpdateEmptyTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(1L, "", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).contains("title cannot be empty");
    }

    @Test
    void failToUpdateEmptyBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(1L, "updated title", "", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).contains("body cannot be empty");
    }

    // fail to update due to changed lineupId
    // ex: update lineupId 1 to a lineup with lineupId 200
    // note the user has to be owner of the lineup we're updating
    // TODO: consider using DTOs, which removes the need for this check
    @Test
    void failToUpdateDueToChangedId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Lineup badUpdatedLineup = new Lineup(33L, "updated title", "updated body", 1L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/lineups/1",
                HttpMethod.PUT,
                new HttpEntity<>(badUpdatedLineup, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).containsIgnoringCase("Cannot change lineup id from '1' to '33'");
    }

    // fail to update due to userId not matching user principal (TODO: after auth)
    // ex: a lineup has userId set to 2, user 2 then tries to update the lineup's id to 5

    // delete
    // successful delete
    @Test
    void successfulDelete() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> res = restTemplate.exchange(
                "/api/lineups/2",
                HttpMethod.DELETE,
                new HttpEntity<>(null, headers),
                void.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
    }
    // unsuccessfully delete TODO: once auth has been impl

    // getByTitle
    // lineupId 5 and 6 share the same title; same name
    @Test
    void successfulGetByTitle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Optional<List<Lineup>>> response = restTemplate.exchange(
                "/api/lineups/search?title=same+name",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent();
        assertThat(response.getBody().get().size()).isEqualTo(2);
        assertThat(response.getBody().get().toString()).isEqualTo("""
                [Lineup[id=5, title=same name, body=bodyFour, userId=3], Lineup[id=6, title=same name, body=bodyFour, userId=3]]""");
    }

    @Test
    void successfulGetByTitleNoMatches() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Optional<List<Lineup>>> response = restTemplate.exchange(
                "/api/lineups/search?title=bad+search",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // test for blanks and empty

    @Test
    void failGetByTitleOnBlankSearchTitle() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/lineups/search?title=",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsIgnoringCase("title cannot be empty");
    }

    @Test
    void failGetByTitleOnEmptySearchTitle() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/lineups/search?title=    ",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsIgnoringCase("title cannot be blank");
    }

    // TODO: add tests where multiple things are missing, say both title and body


    // getByAuthor
    // byAgent
    // byMap
    // combination of all of the above "filters"


}


