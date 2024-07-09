package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.LINEUP;
import static org.jooq.Records.mapping;


@Repository
public class LineupRepository {

    private final DSLContext dsl;

    LineupRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Lineup> findAllLineups() {
        return dsl.select(LINEUP.ID, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .fetch(r -> new Lineup(r.get(LINEUP.ID), r.get(LINEUP.TITLE), r.get(LINEUP.BODY), r.get(LINEUP.USER_ID)));
    }

    public Lineup createLineup(Lineup lineup) {
        createValidation(lineup.title(), lineup.body(), lineup.userId());

        return dsl.insertInto(LINEUP)
                .set(LINEUP.TITLE, lineup.title())
                .set(LINEUP.BODY, lineup.body())
                .set(LINEUP.USER_ID, lineup.userId())
                .returning()
                .fetchOne(record -> new Lineup(record.getId(), record.getTitle(), record.getBody(), record.getUserId()));
    }

    private void createValidation(String title, String body, Long userId) {
        if (title == null) {
            throw new InvalidLineupException.NullTitleException();
        }
        if (body == null) {
            throw new InvalidLineupException.NullBodyException();
        }
        if (userId == null) {
            throw new InvalidLineupException.UserIdNullException();
        }
        if (title.isEmpty()) {
            throw new InvalidLineupException.EmptyTitleException();
        }
        if (body.isEmpty()) {
            throw new InvalidLineupException.EmptyBodyException();
        }
        if (title.isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
        if (body.isBlank()) {
            throw new InvalidLineupException.BlankBodyException();
        }
    }

    // TODO: this is very confusing, please user better names
    private void updateValidation(Long lineupId, String title, String body, Long userId) {
        if (title == null) {
            throw new InvalidLineupException.NullTitleException();
        }
        if (body == null) {
            throw new InvalidLineupException.NullBodyException();
        }
        if (userId == null) {
            throw new InvalidLineupException.UserIdNullException();
        }
        if (lineupId == null) {
            throw new InvalidLineupException.NullLineupIdException();
        }
        if (title.isEmpty()) {
            throw new InvalidLineupException.EmptyTitleException();
        }
        if (body.isEmpty()) {
            throw new InvalidLineupException.EmptyBodyException();
        }
        if (title.isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
        if (body.isBlank()) {
            throw new InvalidLineupException.BlankBodyException();
        }
        if (!lineupId.equals(userId)) {
            throw new InvalidLineupException.UserIdInvalidException();
        }
    }

    public Optional<Lineup> getLineupById(Long id) {
        return dsl.select(LINEUP.ID, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.ID.eq(id))
                .fetchOptional().map(mapping(Lineup::create));
    }

    public void updateLineup(Lineup lineup) {
        updateValidation(lineup.id(), lineup.title(), lineup.body(), lineup.userId());

        dsl.fetchOptional(LINEUP, LINEUP.ID.eq(lineup.id()))
                .ifPresent(r -> {
                    r.setId(lineup.id());
                    r.setTitle(lineup.title());
                    r.setBody(lineup.body()); // I assume that userId is never going to change
                    r.store();
                });
    }

    public void deleteLineup(Long id) {
        if (id == null) {
            throw new InvalidLineupException.NullLineupIdException();
        }
        dsl.deleteFrom(LINEUP).where(LINEUP.ID.eq(id)).execute();
    }
    // consider adding batch delete

    // fetches all the lineups from a given user
    public List<Lineup> getLineupsByUserId(Long id) {
        return dsl.select(LINEUP.ID, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(id))
                .fetch(r -> new Lineup(r.get(LINEUP.ID), r.get(LINEUP.TITLE), r.get(LINEUP.BODY), r.get(LINEUP.USER_ID)));
    }
}