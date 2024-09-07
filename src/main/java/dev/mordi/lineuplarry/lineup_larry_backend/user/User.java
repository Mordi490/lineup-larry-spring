package dev.mordi.lineuplarry.lineup_larry_backend.user;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record User(@Nullable Long id,
                   @Size(min = 1, max = 32, message = "Username has to be between 1 and 32 characters")
                   @NotNull(message = "username cannot be null")
                   @NotEmpty(message = "username cannot be empty")
                   @NotBlank(message = "username cannot be blank")
                   String username) {
}
