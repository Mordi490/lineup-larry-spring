package dev.mordi.lineuplarry.lineup_larry_backend.like;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import dev.mordi.lineuplarry.lineup_larry_backend.like.exceptions.InvalidLikeException;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;

import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.*;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.selectFrom;

@Repository
public class LikeRepository {

    private final DSLContext dsl;

    LikeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Like> getAllLikes() {
        return dsl.selectFrom(LIKES).fetch(r -> r.into(Like.class));
    }

    public Optional<Like> getLikeById(Long lineupId, Long userId) {
        return dsl.select(LIKES.LINEUP_ID, LIKES.USER_ID, LIKES.CREATED_AT)

                .from(LIKES)
                .where(LIKES.USER_ID.eq(userId)).and(LIKES.LINEUP_ID.eq(lineupId))
                .fetchOptional()
                .map(mapping(Like::new));
    }

    public Like likeLineup(Like like) {
        // see if lineup exists
        boolean lineupExistence = dsl
                .fetchExists(selectFrom(LINEUP).where(LINEUP.ID.eq(like.lineupId())));

        // throw if it does not exist
        if (!lineupExistence) {
            throw new InvalidLineupException.NoSuchLineupException(like.lineupId());
        }

        // going to assume that the like.userId cannot be invalid, double check after
        // auth has been
        // added
        // Check if like already exists
        Like existingLike = dsl.selectFrom(LIKES)
                .where(LIKES.LINEUP_ID.eq(like.lineupId()).and(LIKES.USER_ID.eq(like.userId())))
                .fetchOneInto(Like.class);

        // If like already exists, return it (idempotent behavior)
        if (existingLike != null) {
            return existingLike;
        }

        // Insert the like if it does not already exist
        return dsl.insertInto(LIKES).set(LIKES.LINEUP_ID, like.lineupId())
                .set(LIKES.USER_ID, like.userId()).returning()
                .fetchOne(r -> new Like(r.getUserId(), r.getLineupId(), r.getCreatedAt()));
    }

    public void removeLike(Like like) {
        boolean exists = dsl.fetchExists(selectFrom(LIKES).where(LIKES.USER_ID.eq(like.userId()))
                .and(LIKES.LINEUP_ID.eq(like.lineupId())));

        if (!exists) {
            throw new InvalidLikeException.LikeNotFound(like.userId(), like.lineupId());
        }

        dsl.deleteFrom(LIKES).where(LIKES.LINEUP_ID.eq(like.lineupId()))
                .and(LIKES.USER_ID.eq(like.userId())).execute();
    }

    public List<Like> getLikesByUser(Long userId) {
        // confirm that user exist(?)
        boolean exists = dsl.fetchExists(selectFrom(USERS).where(USERS.ID.eq(userId)));

        if (!exists) {
            throw new InvalidUserException.UserNotFoundException(userId);
        }

        return dsl.selectFrom(LIKES).where(LIKES.USER_ID.eq(userId))
                .orderBy(LIKES.CREATED_AT.asc())
                .fetch(r -> new Like(r.getUserId(), r.getLineupId(), r.getCreatedAt()));
    }

    public List<Like> getLikesByLineup(Long lineupId) {
        boolean exists = dsl.fetchExists(selectFrom(LINEUP).where(LINEUP.ID.eq(lineupId)));

        if (!exists) {
            throw new InvalidLineupException.NoSuchLineupException(lineupId);
        }

        return dsl.selectFrom(LIKES).where(LIKES.LINEUP_ID.eq(lineupId))
                .fetch(r -> new Like(r.getUserId(), r.getLineupId(), r.getCreatedAt()));
    }

    public long getLikeCountByLineup(Long lineupId) {
        // confirm that lineup exist(?)
        boolean exists = dsl.fetchExists(selectFrom(LINEUP).where(LINEUP.ID.eq(lineupId)));

        if (!exists) {
            throw new InvalidLineupException.NoSuchLineupException(lineupId);
        }

        Long count = dsl.selectCount()
                .from(LIKES)
                .where(LIKES.LINEUP_ID.eq(lineupId))
                .fetchOne(0, long.class);

        return (count != null) ? count : 0L;
    }

    // TODO: get the total amount of likes a user's lineups have accumulated

}
