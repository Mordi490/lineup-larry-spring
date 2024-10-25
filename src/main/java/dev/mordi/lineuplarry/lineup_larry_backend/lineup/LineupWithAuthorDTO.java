package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;

// TODO: look into whether or not annotations are needed
public record LineupWithAuthorDTO
        (Long id,
         Agent agent,
         Map map,
         String title,
         String body,
         Long userId,
         String authorUsername) {

    public LineupWithAuthorDTO withTitle(String newTitle) {
        return new LineupWithAuthorDTO(this.id, this.agent, this.map, newTitle, this.body, this.userId, this.authorUsername);
    }

    public LineupWithAuthorDTO withBody(String newBody) {
        return new LineupWithAuthorDTO(this.id, this.agent, this.map, this.title, newBody, this.userId, this.authorUsername);
    }
}
