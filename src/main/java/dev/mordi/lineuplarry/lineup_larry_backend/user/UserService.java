package dev.mordi.lineuplarry.lineup_larry_backend.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.getAllUsers();
    }

    public Optional<User> getById(Long id) {
        return userRepository.getUserById(id);
    }

    public User createUser(User user) {
        validateCreateUserData(user);
        return userRepository.createUser(user);
    }

    public void updateUser(Long id, User user) {
        // validate that the id and username has not changed
        validateUpdateUserData(id, user);

        userRepository.updateUser(id, user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    public UserSummaryDTO getUserSummary(Long userId) {
        return userRepository.getUserSummary(userId);
    }

    // Should
    private void validateCreateUserData(User user) {
        validateIdForCreate(user.id());
        validateUsernameForCreate(user.username());
    }

    private void validateIdForCreate(Long id) {
        if (id != null) {
            throw new InvalidUserException.IncludedUserIdException(id);
        }
    }

    private void validateUsernameForCreate(String username) {
        if (username.isEmpty()) {
            throw new InvalidUserException.EmptyUsernameException();
        }
        if (username.isBlank()) {
            throw new InvalidUserException.BlankUsernameException();
        }
    }

    private void validateUpdateUserData(Long id, User user) {
        if (!id.equals(user.id())) {
            throw new InvalidUserException.IdDoesNotMatchUserException(id, user);
        }
        if (user.username() == null) {
            throw new InvalidUserException.NullUsernameException();
        }
        if (user.username().isEmpty()) {
            throw new InvalidUserException.EmptyUsernameException();
        }
        if (user.username().isBlank()) {
            throw new InvalidUserException.BlankUsernameException();
        }
    }
}
