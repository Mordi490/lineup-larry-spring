package dev.mordi.lineuplarry.lineup_larry_backend.user;


import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JooqTest
@Import({UserRepository.class})
@Sql("/test-data.sql")
@Testcontainers
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.3-alpine"
    );

    @Autowired
    UserRepository userRepository;

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
        /*
        assertThrows(InvalidLineupException.NoUserException.class, () -> {
            userRepository.getUserById(22L);
        });
         */
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

        // prev
        /*
        assertThrows(InvalidLineupException.NoUserException.class, () -> {
            userRepository.getUserById(4L);
        });
         */
        Optional<User> reFetchedUser = userRepository.getUserById(4L);
        assertThat(reFetchedUser).isEmpty();
    }

    // TODO: revisit if this is the most common way to go about this
    @Test
    void failDeleteForNonExistingUser() {
        assertThrows(InvalidUserException.UserNotFoundException.class, () -> {
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
}
