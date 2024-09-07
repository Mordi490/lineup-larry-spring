package dev.mordi.lineuplarry.lineup_larry_backend.user;


import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // the unit we want to test
    @InjectMocks
    private UserService userService;

    // dependencies of the unit of testing
    @Mock
    private UserRepository userRepository;

    // might be a better way to go about this
    private User savedUserOne;
    private User savedUserTwo;
    private User userToCreate;
    private User userToCreateWithSetId;
    private User userToFailCreateWithEmptyUsername;
    private User userToFailCreateWithBlankUsername;

    @BeforeEach
    void setUp() {
        savedUserOne = new User(1L, "John");
        savedUserTwo = new User(2L, "Jane");
        userToCreate = new User(null, "newUser");
        userToCreateWithSetId = new User(1L, "newUserFail");
        userToFailCreateWithEmptyUsername = new User(null, "");
        userToFailCreateWithBlankUsername = new User(null, "   ");
    }

    // getAll: success
    @Test
    void getAll() {
        List<User> userList = Arrays.asList(savedUserOne, savedUserTwo);
        when(userRepository.getAllUsers()).thenReturn(userList);

        List<User> result = userService.getAll();

        assertThat(result).isEqualTo(userList);
        verify(userRepository).getAllUsers();
    }

    @Test
    void getAllAfterRemovalOfUser() {
        List<User> userList = Arrays.asList(savedUserTwo);
        when(userRepository.getAllUsers()).thenReturn(userList);

        // delete user1
        userRepository.deleteUser(savedUserOne.id());

        List<User> result = userService.getAll();

        assertThat(result).isEqualTo(userList);
        verify(userRepository).getAllUsers();
    }

    @Test
    void successOnEmptyUserBase() {
        List<User> userList = Arrays.asList();
        when(userRepository.getAllUsers()).thenReturn(userList);

        List<User> result = userService.getAll();

        assertThat(result).isEqualTo(userList);
        verify(userRepository).getAllUsers();
    }

    // geyById: success
    @Test
    void successfulGetById() {
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(savedUserOne));

        Optional<User> fetchedUser = userService.getById(1L);

        assertAll(() -> {
            assertThat(fetchedUser).isPresent();
            assertThat(fetchedUser).isNotNull();
            assertThat(fetchedUser.get().username()).isEqualTo("John");
            assertThat(fetchedUser.get()).isEqualTo(savedUserOne);
        });
        verify(userRepository).getUserById(1L);
    }

    // geyById: "failure"; it returns a a 404
    @Test
    void returnNullForEmptyUserIds() {
        Long nonExistentUserId = 999L;
        when(userRepository.getUserById(nonExistentUserId)).thenReturn(Optional.empty());

        Optional<User> fetchUser = userService.getById(nonExistentUserId);

        assertThat(fetchUser).isNotPresent();
        verify(userRepository).getUserById(nonExistentUserId);
    }

    // createUser: success
    @Test
    void successfulCreateUser() {
        when(userRepository.createUser(userToCreate)).thenReturn(userToCreate);

        User result = userService.createUser(userToCreate);

        assertAll(() -> {
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo("newUser");
        });
        verify(userRepository).createUser(userToCreate);
    }

    // createUser: failure
    // fail creates with id included
    @Test
    void failCreateWithSetId() {
        assertThatThrownBy(() -> userService.createUser(userToCreateWithSetId))
                .isInstanceOf(InvalidUserException.IncludedUserIdException.class)
                .hasMessage("Do not supply id when creating a user\nThe id '" + userToCreateWithSetId.id() + "' is invalid");

        verify(userRepository, never()).createUser(userToCreateWithSetId);
    }

    // fail creates with empty string
    @Test
    void failCreateWithEmptyUsername() {
        assertThatThrownBy(() -> userService.createUser(userToFailCreateWithEmptyUsername))
                .isInstanceOf(InvalidUserException.EmptyUsernameException.class)
                .hasMessage("Username cannot be empty");

        verify(userRepository, never()).createUser(userToFailCreateWithEmptyUsername);
    }

    @Test
    void failCreateWithBlankUsername() {
        assertThatThrownBy(() -> userService.createUser(userToFailCreateWithBlankUsername))
                .isInstanceOf(InvalidUserException.BlankUsernameException.class)
                .hasMessage("Username cannot be blank");

        verify(userRepository, never()).createUser(userToFailCreateWithBlankUsername);
    }

    // updateUser: success
    @Test
    void successfulUpdate() {
        User updatedUser = new User(1L, "Joe");

        doNothing().when(userRepository).updateUser(1L, updatedUser);

        assertThatCode(() -> userService.updateUser(1L, updatedUser)).doesNotThrowAnyException();

        verify(userRepository).updateUser(1L, updatedUser);
    }

    // updateUser: failure
    // mismatched ids
    @Test
    void failUpdateWithMismatchedId() {
        Long id = 4L;
        User userWithMismatchedId = new User(2L, "Jane");

        assertThatThrownBy(() -> userService.updateUser(id, userWithMismatchedId))
                .isInstanceOf(InvalidUserException.IdDoesNotMatchUserException.class)
                .hasMessage("Id does not match for this user\nExpected " + id + " but got " + userWithMismatchedId.id());

        verify(userRepository, never()).updateUser(userWithMismatchedId.id(), userWithMismatchedId);
    }

    // Empty username
    @Test
    void failUpdateForEmptyUsername() {
        User userWithEmptyName = new User(1L, "");

        assertThatThrownBy(() -> userService.updateUser(userWithEmptyName.id(), userWithEmptyName))
                .isInstanceOf(InvalidUserException.EmptyUsernameException.class)
                .hasMessage("Username cannot be empty");

        verify(userRepository, never()).updateUser(userWithEmptyName.id(), userWithEmptyName);
    }

    // blank username
    @Test
    void failUpdateForBlankUsername() {
        User userWithBlankName = new User(1L, "   ");

        assertThatThrownBy(() -> userService.updateUser(1L, userWithBlankName))
                .isInstanceOf(InvalidUserException.BlankUsernameException.class)
                .hasMessage("Username cannot be blank");

        verify(userRepository, never()).updateUser(1L, userWithBlankName);
    }

    // deleteUser: success
    @Test
    void successfulDelete() {
        Long userIdToDelete = 2L;

        doNothing().when(userRepository).deleteUser(userIdToDelete);

        assertThatCode(() -> userService.deleteUser(userIdToDelete)).doesNotThrowAnyException();

        verify(userRepository).deleteUser(userIdToDelete);
    }

    // deleteUser: failure
    // TODO: impl once security has been added
    @Test
    void failToDeleteUserWithoutSufficientAuth() {
    }
}
