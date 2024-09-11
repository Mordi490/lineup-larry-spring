package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

// using Jakarta's bean validator's since they're standardised.


import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Lineup(
        @Nullable
        Long id,

        @NotNull(message = "agent cannot be null")
        Agent agent,

        @NotNull(message = "map cannot be null")
        Map map,

        @NotNull(message = "title cannot be null")
        @NotBlank(message = "title cannot be blank")
        @NotEmpty(message = "title cannot be empty")
        @Size(min = 3, max = 40, message = "Title must be between {min} and {max} characters")
        String title,

        @NotNull(message = "body cannot be null")
        @Size(min = 0, max = 200, message = "A body cannot exceed 200 characters")
        @NotBlank(message = "body cannot be blank")
        @NotEmpty(message = "body cannot be empty")
        String body,

        @NotNull(message = "userId cannot be null")
        Long userId) {

    public Lineup withTitle(String newTitle) {
        return new Lineup(this.id, this.agent, this.map, newTitle, this.body, this.userId);
    }

    public Lineup withAgent(Agent newAgent) {
        return new Lineup(this.id, newAgent, this.map, this.title, this.body, this.userId);
    }

    public Lineup withMap(Map newMap) {
        return new Lineup(this.id, this.agent, newMap, this.title, this.body, this.userId);
    }

    public Lineup withBody(String newBody) {
        return new Lineup(this.id, this.agent, this.map, this.title, newBody, this.userId);
    }

    public Lineup withUserId(Long newUserId) {
        return new Lineup(this.id, this.agent, this.map, this.title, this.body, newUserId);
    }

    public Lineup withId(Long newId) {
        return new Lineup(newId, this.agent, this.map, this.title, this.body, this.userId);
    }
}
