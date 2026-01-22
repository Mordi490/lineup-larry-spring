package dev.mordi.lineuplarry.lineup_larry_backend.user;

import static org.assertj.core.api.Assertions.assertThat;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupIdTitleDTO;
import java.util.List;
import java.util.Optional;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureMockMvc
public class UserIntegrationTest {

  // opting for TestRestTemplate for now, might swap to RestClient once stable
  @Autowired private TestRestTemplate restTemplate;

  private static HttpHeaders headers;

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

  @BeforeAll
  static void initHeader() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  @Test
  void successfulGetAll() {
    ResponseEntity<List<User>> res =
        restTemplate.exchange(
            "/api/users", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    // expect the users insert by the test-data.sql
    List<User> expectedUserList =
        List.of(
            new User(1L, "userOne"),
            new User(2L, "userTwo"),
            new User(3L, "userThree"),
            new User(4L, "userFour"),
            new User(5L, "userFive"));

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNotNull();
    assertThat(res.getBody().size()).isEqualTo(expectedUserList.size());
    assertThat(res.getBody()).isEqualTo(expectedUserList);
  }

  @Test
  void successfulGetById() {
    ResponseEntity<Optional<User>> res =
        restTemplate.exchange(
            "/api/users/1", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    User expectedUser = new User(1L, "userOne");

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNotEmpty();
    assertThat(res.getBody().get()).isEqualTo(expectedUser);
  }

  @Test
  void getByIdOnNonexistentId() {
    ResponseEntity<String> res =
        restTemplate.exchange("/api/users/999", HttpMethod.GET, null, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res.getBody()).isNull();
  }

  @Test
  void failGetByIdOnString() {
    var res = restTemplate.exchange("/api/users/someString", HttpMethod.GET, null, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getBody()).contains("Invalid data");
    assertThat(res.getBody())
        .contains("Invalid value 'someString' for parameter 'id'. Expected type: 'Long'");
  }

  @Test
  void successfulCreate() {
    User newUser = new User(null, "Bobby");

    ResponseEntity<User> res =
        restTemplate.postForEntity("/api/users", new HttpEntity<>(newUser, headers), User.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getBody().id()).isEqualTo(101);
  }

  @Test
  void successfulCreateWithoutId() {
    String userWithNoIdJsonTemplate =
        """
        {"username":"Bobby"}\
        """;

    ResponseEntity<User> res =
        restTemplate.postForEntity(
            "/api/users", new HttpEntity<>(userWithNoIdJsonTemplate, headers), User.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getBody().id()).isNotNull();
    assertThat(res.getBody().id()).isEqualTo(101);
  }

  @Test
  void failCreateOnNullUsername() {
    String userWithNullUsernameJsonTemplate =
        """
        {"username":null}\
        """;

    ResponseEntity<String> res =
        restTemplate.postForEntity(
            "/api/users",
            new HttpEntity<>(userWithNullUsernameJsonTemplate, headers),
            String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.toString()).contains("Invalid data");
    assertThat(res.toString()).contains("username: username cannot be null");
  }

  @Test
  void failCreateOnEmptyUsername() {
    String userWithEmptyUsernameJson =
        """
        {"username":""}\
        """;

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            new HttpEntity<>(userWithEmptyUsernameJson, headers),
            String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getBody()).contains("username: username cannot be empty");
  }

  @Test
  void failCreateOnBlankUsername() {
    String userWithBlankUsernameJson =
        """
        {"username":"  "}\
        """;

    ResponseEntity<String> res =
        restTemplate.postForEntity(
            "/api/users", new HttpEntity<>(userWithBlankUsernameJson, headers), String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getBody()).contains("Invalid data");
    assertThat(res.toString()).contains("username: username cannot be blank");
  }

  @Test
  void successfulUpdate() {
    // "userOne" is the initial name
    User updatedUser = new User(1L, "Bobby");

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/api/users/1", HttpMethod.PUT, new HttpEntity<>(updatedUser, headers), String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNull();

    // confirm that the user was updated in the database
    ResponseEntity<String> res2 =
        restTemplate.exchange("/api/users/1", HttpMethod.GET, null, String.class);

    assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res2.getBody())
        .isEqualTo(
            """
            {"id":1,"username":"Bobby"}\
            """);
  }

  @Test
  void failUpdateOnNullUsername() {
    User updatedUser = new User(1L, null);

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/api/users/1", HttpMethod.PUT, new HttpEntity<>(updatedUser, headers), String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getBody()).contains("Invalid data");
    assertThat(res.getBody()).contains("username: username cannot be null");
  }

  @Test
  void failUpdateOnEmptyUsername() {
    User updatedUser = new User(1L, "");

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/api/users/1", HttpMethod.PUT, new HttpEntity<>(updatedUser, headers), String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getBody()).contains("Invalid data");
    assertThat(res.getBody()).contains("username: username cannot be empty");
  }

  @Test
  void failUpdateOnBlankUsername() {
    User updatedUser = new User(1L, "   ");

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/api/users/1", HttpMethod.PUT, new HttpEntity<>(updatedUser, headers), String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(res.getBody()).contains("Invalid data");
    assertThat(res.getBody()).contains("username: username cannot be blank");
  }

  // deleteUser
  @Test
  void successfulDeleteUserWithNoLineups() {
    ResponseEntity<String> res =
        restTemplate.exchange("/api/users/4", HttpMethod.DELETE, null, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  // When a user gets deleted the lineups created by the user will also get deleted
  // for reference the userId 2 has two lineups, with id 2 and 3.
  @Test
  void successfulDeleteUserWithLineups() {
    // confirm that the user does have lineups
    ResponseEntity<String> userToDeletesLineups =
        restTemplate.exchange("/api/lineups/user/2", HttpMethod.GET, null, String.class);

    assertThat(userToDeletesLineups.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userToDeletesLineups.getBody()).isNotEmpty();

    // delete the user
    var deleteUserResponse =
        restTemplate.exchange("/api/users/2", HttpMethod.DELETE, null, String.class);

    assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // confirm that the user has been deleted
    var requestDeletedUser =
        restTemplate.exchange("/api/user/2", HttpMethod.GET, null, String.class);

    assertThat(requestDeletedUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    // confirm that the lineups also have been deleted
    var requestFirstLineup =
        restTemplate.exchange("/api/lineups/2", HttpMethod.GET, null, String.class);

    var requestSecondLineup =
        restTemplate.exchange("/api/lineups/3", HttpMethod.GET, null, String.class);

    assertThat(requestFirstLineup.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(requestSecondLineup.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void failDeleteOnNonexistentId() {
    // we'd expect to receive a 404 w/msg "no user associated with id XZY"-esq
    // for reference, userId: 2 has lineups (ids) 2 and 3.
    ResponseEntity<String> res =
        restTemplate.exchange("/api/users/222", HttpMethod.DELETE, null, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res.getBody()).contains("User not found");
    assertThat(res.getBody()).contains("User with id: '222' was not found");
  }

  // unsuccessful delete TODO: once auth has been impl

  // getUserSummary
  @Test
  void successfulGetUserSummary() {
    ResponseEntity<UserSummaryDTO> res =
        restTemplate.exchange(
            "/api/users/summary/3", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    // expeceted resutls:
    List<LineupIdTitleDTO> recentlyCreatedLineups =
        List.of(
            new LineupIdTitleDTO(22L, "titleFour"),
            new LineupIdTitleDTO(23L, "titleFour"),
            new LineupIdTitleDTO(24L, "titleFour"),
            new LineupIdTitleDTO(25L, "titleFour"),
            new LineupIdTitleDTO(26L, "titleFour"));

    List<LineupIdTitleDTO> mostLikedLineups =
        List.of(
            new LineupIdTitleDTO(22L, "titleFour"),
            new LineupIdTitleDTO(20L, "titleFour"),
            new LineupIdTitleDTO(14L, "titleFour"),
            new LineupIdTitleDTO(11L, "sick pop flash"),
            new LineupIdTitleDTO(4L, "lineupFour"));

    List<LineupIdTitleDTO> recentlyLikedLineups =
        List.of(
            new LineupIdTitleDTO(1L, "lineupOne"),
            new LineupIdTitleDTO(20L, "titleFour"),
            new LineupIdTitleDTO(9L, "teleport thingy"),
            new LineupIdTitleDTO(15L, "titleFour"),
            new LineupIdTitleDTO(22L, "titleFour"));

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody().userId()).isEqualTo(3);
    assertThat(res.getBody().recentLineups()).isEqualTo(recentlyCreatedLineups);
    assertThat(res.getBody().mostLikedLineups()).isEqualTo(mostLikedLineups);
    assertThat(res.getBody().recentlyLikedLineups()).isEqualTo(recentlyLikedLineups);
  }

  @Test
  void getUserSummaryOnUserWithNoData() {
    ResponseEntity<UserSummaryDTO> res =
        restTemplate.exchange(
            "/api/users/summary/5", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNotNull();
    assertThat(res.getBody().username()).isEqualTo("userFive");
    assertThat(res.getBody().userId()).isEqualTo(5);
    assertThat(res.getBody().recentLineups()).isEmpty();
    assertThat(res.getBody().mostLikedLineups()).isEmpty();
    assertThat(res.getBody().recentlyLikedLineups()).isEmpty();
  }

  @Test
  void getUserSummaryOnNonexistentUser() {
    ResponseEntity<String> res =
        restTemplate.exchange("/api/users/summary/999", HttpMethod.GET, null, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
