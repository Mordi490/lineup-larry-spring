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
        assertThat(res.getBody()).isPresent().isNotEmpty();
        assertThat(res.getBody().get()).isEqualTo(new User(1L, "userOne"));
    }

    // getNonexistentUser
    @Test
    void getByIdOnNonexistentId() {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/999",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.toString()).containsIgnoringCase("User not found");
        assertThat(res.toString()).containsIgnoringCase("User with id: '999' was not found");
    }

    @Test
    void faiGetByIdOnString() {
        var res = restTemplate.exchange(
                "/api/users/someString",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).contains("Invalid data");
        assertThat(res.getBody()).contains("Invalid value 'someString' for parameter 'id'. Expected type: 'Long'");
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
        assertThat(res.getBody()).contains("username: username cannot be null");
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
        assertThat(res.getBody()).containsIgnoringCase("username cannot be empty");
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


        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsIgnoringCase("username cannot be blank");
    }

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

    // When a user gets deleted, we'll also the delete the lineups the user has created
    // for reference the userId 2 has two lineups, with id 2 and 3.
    @Test
    void successfulDeleteAUserWithLineups() {
        // confirm that the user does have lineups
        var userToDeletesLineups = restTemplate.exchange(
                "/api/lineups/user/2",
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(userToDeletesLineups.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userToDeletesLineups.getBody()).isNotEmpty();

        // delete the user
        var deleteUserResponse = restTemplate.exchange(
                "/api/users/2",
                HttpMethod.DELETE,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // confirm that the user has been deleted
        var requestDeletedUser = restTemplate.exchange(
                "/api/user/2",
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(requestDeletedUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // confirm that the lineups also have been deleted
        var requestFirstLineup = restTemplate.exchange(
                "/api/lineups/2",
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        var requestSecondLineup = restTemplate.exchange(
                "/api/lineups/3",
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(requestFirstLineup.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(requestSecondLineup.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void failDeleteOnNonexistentId() {
        // we'd expect to receive a 404 w/msg "no user associated with id XZY"-esq
        // for reference, userId: 2 has lineups (ids) 2 and 3.
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/users/222",
                HttpMethod.DELETE,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("User with id: '222' was not found");
    }

    // unsuccessful delete TODO: once auth has been impl
}
