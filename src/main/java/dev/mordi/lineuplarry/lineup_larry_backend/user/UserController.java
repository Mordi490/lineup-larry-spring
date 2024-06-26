package dev.mordi.lineuplarry.lineup_larry_backend.user;
import jakarta.validation.Valid;
import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return repository.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(User user) {
        System.out.println("Received user (controller): " + user);
        repository.createUser(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, User user) {
        repository.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        repository.deleteUser(id);
    }
}
