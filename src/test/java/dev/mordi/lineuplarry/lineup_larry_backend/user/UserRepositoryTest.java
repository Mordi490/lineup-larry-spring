package dev.mordi.lineuplarry.lineup_larry_backend.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupIdTitleDTO;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@JooqTest
@Import({UserRepository.class})
@Sql("/test-data.sql")
@Testcontainers
public class UserRepositoryTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

  @Autowired UserRepository userRepository;

  @Test
  void dbHasBeenPopulated() {
    List<User> allUsers = userRepository.getAllUsers();

    assertThat(allUsers.size()).isEqualTo(5);
  }

  @Test
  void successfulGetByIdExistingUser() {
    Optional<User> possibleUser = userRepository.getUserById(1L);

    assertThat(possibleUser).isPresent();
    assertThat(possibleUser.get().username()).isEqualTo("userOne");
    assertThat(possibleUser.get().id()).isEqualTo(1L);
  }

  @Test
  void getByIdOnNonexistentUser() {
    Optional<User> user = userRepository.getUserById(22L);

    assertThat(user).isEmpty();
  }

  @Test
  void successfulUserCreation() {
    User user = new User(null, "bob");

    User savedUser = userRepository.createUser(user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.username()).isEqualTo("bob");
    assertThat(savedUser.id()).isEqualTo(101L);
  }

  @Test
  void successfulUpdateTest() throws Exception {
    Optional<User> userToUpdate = userRepository.getUserById(1L);

    if (userToUpdate.isPresent()) {
      assertThat(userToUpdate.get().username()).isEqualTo("userOne");
      User updatedUserData = new User(1L, "billy");
      userRepository.updateUser(updatedUserData.id(), updatedUserData);
      Optional<User> fetchUpdatedUser = userRepository.getUserById(1L);
      if (fetchUpdatedUser.isPresent()) {
        assertThat(fetchUpdatedUser.get().username()).isEqualTo("billy");
      } else {
        throw new Exception("failed to fetch the updated user");
      }
    } else {
      throw new Exception("failed to fetch existing user");
    }
  }

  @Test
  void successfulDeleteUserWithNoLineups() {
    Optional<User> userToDelete = userRepository.getUserById(4L);
    assertThat(userToDelete).isPresent();

    userRepository.deleteUser(4L);

    Optional<User> reFetchedUser = userRepository.getUserById(4L);
    assertThat(reFetchedUser).isEmpty();
  }

  @Test
  void failDeleteForNonExistingUser() {
    assertThrows(
        InvalidUserException.UserNotFoundException.class,
        () -> {
          userRepository.deleteUser(22L);
        });
  }

  // delete user with lineups, cascading, meaning lineups tied to this user also gets delete
  // NB! simply tests if postgres permits the deletion of a user with lineups
  @Test
  void successfulDeleteUserWithLineups() {
    Optional<User> userWithLineups = userRepository.getUserById(2L);
    // for reference the user (2) has lineup (ids) 2 and 3
    assertThat(userWithLineups).isPresent();

    userRepository.deleteUser(2L);
  }

  // getUserSummary
  @Test
  void successfulGetUserSummary() {
    UserSummaryDTO userSummary = userRepository.getUserSummary(3L);

    // expected Data:
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

    UserSummaryDTO expectedResult =
        new UserSummaryDTO(
            3L, "userThree", recentlyCreatedLineups, mostLikedLineups, recentlyLikedLineups);

    assertThat(userSummary).isEqualTo(expectedResult);
  }

  @Test
  void getUserSummaryOnUserWithNoData() {
    UserSummaryDTO userSummaryDTO = userRepository.getUserSummary(5L);

    List<LineupIdTitleDTO> emptyList = List.of();

    assertThat(userSummaryDTO.userId()).isEqualTo(5L);
    assertThat(userSummaryDTO.username()).isEqualTo("userFive");
    assertThat(userSummaryDTO.recentLineups()).isEqualTo(emptyList);
    assertThat(userSummaryDTO.mostLikedLineups()).isEqualTo(emptyList);
    assertThat(userSummaryDTO.recentlyLikedLineups()).isEqualTo(emptyList);
  }

  @Test
  void getUserSummaryOnNonexistentUser() {
    assertThrows(
        InvalidUserException.UserNotFoundException.class,
        () -> {
          userRepository.getUserSummary(999L);
        });
  }
}
