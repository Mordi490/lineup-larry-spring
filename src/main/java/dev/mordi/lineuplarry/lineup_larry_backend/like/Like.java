package dev.mordi.lineuplarry.lineup_larry_backend.like;

import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;

public record Like(Long userId, Long lineupId, @Nullable OffsetDateTime createdAt) {}
