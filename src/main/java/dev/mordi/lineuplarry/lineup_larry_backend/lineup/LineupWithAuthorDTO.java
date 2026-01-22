package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import java.time.OffsetDateTime;

public record LineupWithAuthorDTO(
    Long id,
    Agent agent,
    Map map,
    String title,
    String body,
    Long userId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    String authorUsername) {

  public LineupWithAuthorDTO withTitle(String newTitle) {
    return new LineupWithAuthorDTO(
        this.id,
        this.agent,
        this.map,
        newTitle,
        this.body,
        this.userId,
        this.createdAt,
        this.updatedAt,
        this.authorUsername);
  }

  public LineupWithAuthorDTO withBody(String newBody) {
    return new LineupWithAuthorDTO(
        this.id,
        this.agent,
        this.map,
        this.title,
        newBody,
        this.userId,
        this.createdAt,
        this.updatedAt,
        this.authorUsername);
  }
}
