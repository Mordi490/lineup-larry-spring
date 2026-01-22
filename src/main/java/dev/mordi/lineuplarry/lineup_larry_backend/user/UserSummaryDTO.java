package dev.mordi.lineuplarry.lineup_larry_backend.user;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupIdTitleDTO;
import java.util.List;

public record UserSummaryDTO(
    Long userId,
    String username,
    List<LineupIdTitleDTO> recentLineups,
    List<LineupIdTitleDTO> mostLikedLineups,
    List<LineupIdTitleDTO> recentlyLikedLineups) {}
