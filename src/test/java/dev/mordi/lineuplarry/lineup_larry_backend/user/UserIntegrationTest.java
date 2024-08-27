package dev.mordi.lineuplarry.lineup_larry_backend.user;

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
public class UserIntegrationTest {

    // opting for TestRestTemplate for now, might swap to RestClient once stable
    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.3-alpine"
    );

    // getAll
    @Test
    void successfulGetAll() {
        ResponseEntity<List<User>> res = restTemplate.exchange(
                "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().size()).isEqualTo(5);
        assertThat(res.getBody().toString()).contains("userOne", "userTwo", "userThree", "userFour", "userFive");
    }

    // getUserById
    // getExistingUser
    @Test
    void successfulGetUserById() {
        ResponseEntity<Optional<User>> res = restTemplate.exchange(
                "/api/users/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Optional<User>>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().isPresent()).isNotNull();
        assertThat(res.getBody().get()).isEqualTo(new User(1L, "userOne"));
    }

    // getNonexistentUser
    @Test
    void getFromNonexistentId() {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/999",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.toString()).containsIgnoringCase("User not found");
        assertThat(res.toString()).containsIgnoringCase("User with id: '999' was not found");
    }

    // createUser
    @Test
    void successfulCreateUser() {
        User newUser = new User(null, "Bobby");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<User> res = restTemplate.postForEntity(
                "/api/users",
                new HttpEntity<>(newUser, headers),
                User.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().id()).isNotNull();
        assertThat(res.getBody().id()).isEqualTo(101);
    }

    @Test
    void successfulCreateWithoutId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<User> res = restTemplate.postForEntity(
                "/api/users",
                new HttpEntity<>("""
                        {"username":"Bobby"}""", headers),
                User.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().id()).isNotNull();
        assertThat(res.getBody().id()).isEqualTo(101);
    }

    // fail on null, empty and blank
    @Test
    void failCreateOnNullUsername() {
        // username as null, gets read as {}
        User userWithNullUsername = new User(null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/users",
                new HttpEntity<>(userWithNullUsername, headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.toString()).containsIgnoringCase("Invalid data");
        assertThat(res.toString()).containsIgnoringCase("username: username cannot be null");
    }

    @Test
    void failCreateOnEmptyUsername() {
        User userWithEmptyUsername = new User(null, "");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<User> res = restTemplate.exchange(
                "/api/users",
                HttpMethod.POST,
                new HttpEntity<>(userWithEmptyUsername, headers),
                User.class
        );

        System.out.println("res string: " + res.toString());
        System.out.println("res.getBody string: " + res.getBody());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // TODO: double check problemDetails via curl or http
    // TODO: write a todo about restTemplate and redirects
    @Test
    void failCreateOnBlankUsername() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/users",
                new HttpEntity<>("""
                        {"username":"  "}""", headers),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // pls gib redirects
        assertThat(res.toString()).containsIgnoringCase("username cannot be blank");
    }

    // updateUser
    @Test
    void successfulUpdate() {
        User updatedUser = new User(1L, "Bobby");

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNull();
    }

    // TODO: double check that we get the correct error messages
    // fail to update user due to null, blank, empty, wrong principal.
    @Test
    void failUpdateOnNullUsername() {
        User updatedUser = new User(1L, null);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).contains("username: username cannot be null");
    }

    @Test
    void failUpdateOnEmptyUsername() {
        User updatedUser = new User(1L, "");

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).containsIgnoringCase("username cannot be empty");
    }

    @Test
    void failUpdateOnBlankUsername() {
        User updatedUser = new User(1L, "   ");

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/1",
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                String.class
        );

        System.out.println("update blank res: " + res.toString());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody().toString()).containsIgnoringCase("username cannot be blank");
    }

    // TODO: figure out the sql jackass
    // deleteUser
    @Test
    void successfulDeleteAUserWithNoLineups() {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/4",
                HttpMethod.DELETE,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    // TODO: test cascading effects of deleting a user
    @Test
    void successfulDeleteAUserWithLineups() {
        // TODO: decide on whether or not we cascade on lineups
    }

    // unsuccessful delete TODO: once auth has been impl
}
