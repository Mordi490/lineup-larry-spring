package dev.mordi.lineuplarry.lineup_larry_backend.like.exceptions;

import dev.mordi.lineuplarry.lineup_larry_backend.shared.ApiProblemException;
import org.springframework.http.HttpStatus;

public abstract class InvalidLikeException extends ApiProblemException {

    protected InvalidLikeException(
        HttpStatus status,
        String problemSlug,
        String title,
        String detail,
        String code
    ) {
        super(status, problemSlug, title, detail, code);
    }

    public static class LikeNotFound extends InvalidLikeException {

        // TODO: reconsider if this is the best way to do this
        public LikeNotFound(Long userId, Long lineupId) {
            super(
                HttpStatus.NOT_FOUND,
                "likes/not-found",
                "Like not found",
                String.format(
                    "The like between userId: '%d' and lineupId '%d' does not exist",
                    userId,
                    lineupId
                ),
                "LIKE_NOT_FOUND"
            );
        }
    }
}
