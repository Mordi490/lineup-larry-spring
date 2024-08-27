package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

// using Jakarta's bean validator's since they're standardised.


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

// TODO: add validation, this might change the outcome of a lot of tests...
public record Lineup(
        @Nullable
        Long id,

        @NotNull(message = "title cannot be null")
        @NotBlank(message = "title cannot be blank")
        @NotEmpty(message = "title cannot be empty")
        @Size(min = 3, max = 40, message = "Title must be between 3 and 40 characters")
        String title,

        @NotNull(message = "body cannot be null")
        @Size(min = 0, max = 200, message = "A body cannot exceed 200 characters")
        @NotBlank(message = "body cannot be blank")
        @NotEmpty(message = "body cannot be empty")
        String body,

        @NotNull(message = "userId cannot be null")
        Long userId) {

    // helper factory method
    public static Lineup create(Long id, String title, String body, Long userId) {
        return new Lineup(id, title, body, userId);
    }

    // Method to recreate everything but the title
    public Lineup withTitle(String newTitle) {
        return new Lineup(this.id, newTitle, this.body, this.userId);
    }

    // Method to recreate everything but the body
    public Lineup withBody(String newBody) {
        return new Lineup(this.id, this.title, newBody, this.userId);
    }

    // Method to recreate everything but the userId
    public Lineup withUserId(Long newUserId) {
        return new Lineup(this.id, this.title, this.body, newUserId);
    }

    // Method to recreate everything but the id
    public Lineup withId(Long newId) {
        return new Lineup(newId, this.title, this.body, this.userId);
    }
}
