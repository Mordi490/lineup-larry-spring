package dev.mordi.lineuplarry.lineup_larry_backend.user;

import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService
            .getById(id)
            .orElseThrow(() ->
                new InvalidUserException.UserNotFoundException(id)
            );
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public void updateUser(
        @PathVariable Long id,
        @Valid @RequestBody User user
    ) {
        userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<UserSummaryDTO> getUserSummary(
        @PathVariable Long id
    ) {
        UserSummaryDTO ar = userService.getUserSummary(id);
        return new ResponseEntity<>(ar, HttpStatus.OK);
    }
    // TODO: after auth:
}
