package dev.mordi.lineuplarry.lineup_larry_backend.user;

import java.util.List;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupIdTitleDTO;

public record UserSummaryDTO(Long userId, String username, List<LineupIdTitleDTO> recentLineups,
        List<LineupIdTitleDTO> mostLikedLineups, List<LineupIdTitleDTO> recentlyLikedLineups) {
}
