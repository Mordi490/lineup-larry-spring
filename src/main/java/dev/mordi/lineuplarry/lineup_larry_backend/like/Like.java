package dev.mordi.lineuplarry.lineup_larry_backend.like;

import java.time.OffsetDateTime;

import jakarta.annotation.Nullable;

public record Like(Long userId, Long lineupId, @Nullable OffsetDateTime createdAt) {
}
