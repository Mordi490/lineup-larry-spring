package dev.mordi.lineuplarry.lineup_larry_backend.user;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper om;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User(1L, "John Doe");
    }

    // getAll
    @Test
    void shouldReturnSomeUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(new User(1L, "userOne"), new User(2L, "userTwo")));

        var result = mockMvc.perform(get("/api/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()).andReturn();

        assertThat(result.getResponse().getContentType()).isEqualToIgnoringCase("application/json");
        assertThat(result.getResponse().getContentAsString()).contains("userOne", "userTwo");
        verify(userService).getAll();
    }

    // getById
    // success
    @Test
    void getFirstUser() throws Exception {
        Long userIdToFetch = 1L;
        when(userService.getById(userIdToFetch)).thenReturn(Optional.of(user));

        var result = mockMvc.perform(get("/api/users/{id}", userIdToFetch))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(user.username())).andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("John Doe");
        verify(userService).getById(userIdToFetch);
    }

    // failure
    @Test
    void getByIdOnNonexistentUser() throws Exception {
        Long nonExistentUserId = 444L;
        when(userService.getById(nonExistentUserId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", nonExistentUserId)).andExpect(status().isNotFound());

        verify(userService).getById(nonExistentUserId);
    }

    // createUser
    @Test
    void successfulCreationOfNewUser() throws Exception {
        User userToCreate = new User(null, "newUser");
        User expectedCreatedUser = new User(3L, "newUser");
        when(userService.createUser(userToCreate)).thenReturn(expectedCreatedUser);
        String userToCreateJson = om.writeValueAsString(userToCreate);
        String expectedCreateUserJson = om.writeValueAsString(expectedCreatedUser);

        var result = mockMvc
                .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userToCreateJson))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3)).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedCreateUserJson);
        verify(userService).createUser(userToCreate);
    }

    @Test
    void declineUserCreationWithSuppliedId() throws Exception {
        Long userIdToInvalidate = 44L;
        User userToFailToCreate = new User(userIdToInvalidate, "newUser");
        InvalidUserException.IncludedUserIdException exception = new InvalidUserException.IncludedUserIdException(
                userIdToInvalidate);

        when(userService.createUser(userToFailToCreate)).thenThrow(exception);

        String userToFailToCreateJson = om.writeValueAsString(userToFailToCreate);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userToFailToCreateJson))
                .andExpect(status().isBadRequest()) // Assuming you
                                                    // return 400
                                                    // Bad Request
                                                    // for this
                                                    // exception
                .andExpect(jsonPath("$.title").value("The userId provided is not valid"))
                .andExpect(
                        jsonPath("$.detail").value("Do not supply id when creating a user\nThe id '"
                                + userIdToInvalidate + "' is invalid"))
                .andReturn();

        verify(userService).createUser(userToFailToCreate);
    }

    @Test
    void failToCreateEmptyUsernameForCreation() throws Exception {
        User userWithEmptyUsername = new User(null, "");
        InvalidUserException.EmptyUsernameException exception = new InvalidUserException.EmptyUsernameException();

        when(userService.createUser(userWithEmptyUsername)).thenThrow(exception);

        String userWithEmptyUsernameJson = om.writeValueAsString(userWithEmptyUsername);

        var res = mockMvc
                .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userWithEmptyUsernameJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid data")).andReturn();

        assertThat(res.getResponse().getContentAsString())
                .contains("username: username cannot be empty");
        assertThat(res.getResponse().getContentType())
                .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
        verify(userService, never()).createUser(userWithEmptyUsername);
    }

    @Test
    void failToCreateBlankUsernameForCreation() throws Exception {
        User userWithBlankUsername = new User(null, "  ");
        InvalidUserException.BlankUsernameException exception = new InvalidUserException.BlankUsernameException();

        when(userService.createUser(userWithBlankUsername)).thenThrow(exception);

        String userWithBlankUsernameJson = om.writeValueAsString(userWithBlankUsername);

        var res = mockMvc
                .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userWithBlankUsernameJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid data")).andReturn();

        assertThat(res.getResponse().getContentAsString())
                .contains("username: username cannot be blank");
        assertThat(res.getResponse().getContentType())
                .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
        verify(userService, never()).createUser(userWithBlankUsername);
    }

    @Test
    void failCreateDueToEmptyUsername() throws Exception {
        User userWithEmptyUsername = new User(null, "");
        InvalidUserException.EmptyUsernameException exception = new InvalidUserException.EmptyUsernameException();

        when(userService.createUser(userWithEmptyUsername)).thenThrow(exception);

        try {
            String userWithEmptyUsernameJson = om.writeValueAsString(userWithEmptyUsername);

            var res = mockMvc
                    .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                            .content(userWithEmptyUsernameJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data")).andReturn();

            assertThat(res.getResponse().getContentAsString())
                    .contains("username: username cannot be empty");
            assertThat(res.getResponse().getContentType())
                    .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(userService, never()).createUser(userWithEmptyUsername);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    // updateUser
    @Test
    void successfulUpdate() throws Exception {
        Long userId = 1L;
        User updatedUser = new User(userId, "Johnathon Donald");

        doNothing().when(userService).updateUser(userId, updatedUser);

        try {
            String updateUserJson = om.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateUserJson))
                    .andExpect(status().isOk());

            verify(userService).updateUser(userId, updatedUser);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void declineMismatchedUserIdOnUpdate() throws Exception {
        Long nonExistentUserId = 999L;
        User updatedUser = new User(1L, "Johnathon Donald");

        InvalidUserException.IdDoesNotMatchUserException exception = new InvalidUserException.IdDoesNotMatchUserException(
                nonExistentUserId, updatedUser);
        doThrow(exception).when(userService).updateUser(nonExistentUserId, updatedUser);

        try {
            String updatedUserJson = om.writeValueAsString(updatedUser);

            mockMvc.perform(put("/api/users/{id}", nonExistentUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedUserJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("UserId is not valid"))
                    .andExpect(jsonPath("$.detail")
                            .value("Id does not match for this user\nExpected: '"
                                    + nonExistentUserId + "' but got: '" + updatedUser.id() + "'"))
                    .andReturn();

            verify(userService).updateUser(nonExistentUserId, updatedUser);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnNullUsername() throws Exception {
        User userWithNullUsername = new User(1L, null);
        InvalidUserException.NullUsernameException exception = new InvalidUserException.NullUsernameException();
        doThrow(exception).when(userService).updateUser(userWithNullUsername.id(),
                userWithNullUsername);
        try {
            String userWithNullUsernameJson = om.writeValueAsString(userWithNullUsername);

            var res = mockMvc
                    .perform(put("/api/users/{id}", userWithNullUsername.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userWithNullUsernameJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data")) // Should this be missing
                                                                          // instead of null?
                    .andReturn();

            assertThat(res.getResponse().getContentAsString())
                    .contains("username: username cannot be null");
            assertThat(res.getResponse().getContentType())
                    .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(userService, never()).updateUser(userWithNullUsername.id(),
                    userWithNullUsername);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnEmptyUsername() throws Exception {
        User userWithEmptyUsername = new User(1L, "");

        InvalidUserException.EmptyUsernameException exception = new InvalidUserException.EmptyUsernameException();

        doThrow(exception).when(userService).updateUser(userWithEmptyUsername.id(),
                userWithEmptyUsername);

        try {
            String userWithEmptyUsernameJson = om.writeValueAsString(userWithEmptyUsername);

            var res = mockMvc
                    .perform(put("/api/users/{id}", userWithEmptyUsername.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userWithEmptyUsernameJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data")).andReturn();

            assertThat(res.getResponse().getContentAsString())
                    .contains("username: username cannot be empty");
            assertThat(res.getResponse().getContentType())
                    .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(userService, never()).updateUser(userWithEmptyUsername.id(),
                    userWithEmptyUsername);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnBlankUsername() throws Exception {
        Long userId = 1L;
        User userWithBlankUsername = new User(userId, "  ");

        InvalidUserException.BlankUsernameException exception = new InvalidUserException.BlankUsernameException();
        doThrow(exception).when(userService).updateUser(userWithBlankUsername.id(),
                userWithBlankUsername);

        try {
            String userWithBlankUsernameJson = om.writeValueAsString(userWithBlankUsername);

            var res = mockMvc
                    .perform(put("/api/users/{id}", userId).contentType(MediaType.APPLICATION_JSON)
                            .content(userWithBlankUsernameJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data")).andReturn();

            assertThat(res.getResponse().getContentAsString())
                    .contains("username: username cannot be blank");
            assertThat(res.getResponse().getContentType())
                    .isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(userService, never()).updateUser(userWithBlankUsername.id(),
                    userWithBlankUsername);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    // deleteUser
    // TODO: add failure cases as soon as Spring Security is added
    @Test
    void successfulDeletion() throws Exception {
        Long userIdToDelete = 1L;

        doNothing().when(userService).deleteUser(userIdToDelete);

        mockMvc.perform(delete("/api/users/{id}", userIdToDelete))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userIdToDelete);
    }
}
