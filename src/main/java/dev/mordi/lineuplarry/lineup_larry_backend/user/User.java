package dev.mordi.lineuplarry.lineup_larry_backend.user;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record User(Long id, String username) {
    // helper factory method
    public static User create(Long id, @NotNull(message = "username cannot be null") @Min(value = 1, message = "username must contain at least one character") String username) {
        return new User(id, username);
    }
}
