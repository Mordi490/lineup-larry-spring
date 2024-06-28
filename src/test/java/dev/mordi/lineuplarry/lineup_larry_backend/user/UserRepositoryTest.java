package dev.mordi.lineuplarry.lineup_larry_backend.user;


import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.IdDoesNotMatchUserException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUsernameException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.UserNotFoundException;
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
    void successfulGetById() {
        Optional<User> possibleUser = userRepository.getUserById(1L);
        assertThat(possibleUser.get().username()).isEqualTo("userOne");
        assertThat(possibleUser.get().id()).isEqualTo(1L);
    }

    @Test
    void negativeGetById() {
        Optional<User> shouldNotExist = userRepository.getUserById(22L);
        assertThat(shouldNotExist.isEmpty());
    }

    @Test
    void successfulUserCreation() {
        User user = new User(null, "bob");

        User savedUser = userRepository.createUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.username()).isEqualTo("bob");
        assertThat(savedUser.id()).isEqualTo(101L);
    }

    // TODO: Set SQL constraints so we get validation
    @Test
    void rejectNullValuesUserCreation() {
        User user = new User(null, null);
        assertThrows(InvalidUsernameException.NullUsernameException.class, () -> {
            userRepository.createUser(user);
        });
    }

    @Test
    void rejectEmptyStringUserCreation() {
        User user = new User(null, "");
        // Assert that createUser throws an EmptyUsernameException
        assertThrows(InvalidUsernameException.EmptyUsernameException.class, () -> {
            userRepository.createUser(user);
        });
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
    // TODO: negative test cases for update?

    @Test
    void failUpdateIfIdIsChanged() throws Exception {
        // TODO
        Optional<User> userToUpdate = userRepository.getUserById(2L);

        if (userToUpdate.isEmpty()) {
            throw new Exception("expected user with id 2 to exist");
        }

        User userWithChangedId = new User(3L, userToUpdate.get().username());

        assertThrows(IdDoesNotMatchUserException.class, () -> {
            userRepository.updateUser(2L, userWithChangedId);
        });
    }

    @Test
    void failIfUpdateUserIfNameIsNull() throws Exception {
        Optional<User> userToUpdate = userRepository.getUserById(2L);

        if (userToUpdate.isEmpty()) {
            throw new Exception("expected user with id 2 to exist");
        }

        User userWithNullName = new User(2L, null);

        assertThrows(InvalidUsernameException.NullUsernameException.class, () -> {
            userRepository.updateUser(2L, userWithNullName);
        });
    }

    @Test
    void failIfUpdateUserIfNameIsEmpty() throws Exception {
        Optional<User> userToUpdate = userRepository.getUserById(2L);

        if (userToUpdate.isEmpty()) {
            throw new Exception("expected user with id 2 to exist");
        }

        User userWithBlankName = new User(2L, " ");

        assertThrows(InvalidUsernameException.BlankUsernameException.class, () -> {
            userRepository.updateUser(2L, userWithBlankName);
        });
    }

    @Test
    void successfulDelete() {
        Optional<User> userToDelete = userRepository.getUserById(4L);
        assertThat(userToDelete.isPresent()).isTrue();

        userRepository.deleteUser(4L);

        Optional<User> deletedUser = userRepository.getUserById(4L);
        assertThat(deletedUser.isEmpty()).isTrue();
    }

    @Test
    void failDeleteForNonExistingUser() {
        assertThrows(UserNotFoundException.class, () -> {
            userRepository.deleteUser(22L);
        });
    }
}
