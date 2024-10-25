package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.LINEUP;
import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.USERS;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.selectFrom;


@Repository
public class LineupRepository {

    private final DSLContext dsl;

    LineupRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // TODO: review and rename these
    public Lineup createLineup(Lineup lineup) {
        if (doesUserExist(lineup.userId(), true)) {
            return dsl.insertInto(LINEUP)
                    .set(LINEUP.TITLE, lineup.title())
                    .set(LINEUP.AGENT, lineup.agent())
                    .set(LINEUP.MAP, lineup.map())
                    .set(LINEUP.BODY, lineup.body())
                    .set(LINEUP.USER_ID, lineup.userId())
                    .returning()
                    .fetchOne(mapping(Lineup::new));
        } else {
            throw new RuntimeException("Failed to create lineup");
        }
    }

    // TODO: refactor this to not be stupid
    protected boolean doesUserExist(Long userId, boolean isCreate) {
        boolean exists = dsl.fetchExists(
                selectFrom(USERS).where(USERS.ID.eq(userId)));

        if (!exists && !isCreate) {
            throw new InvalidLineupException.NoUserException(userId);
        }
        if (!exists) {
            throw new InvalidLineupException.UserIdInvalidException(userId);
        }
        return true;
    }

    public Optional<LineupWithAuthorDTO> getLineupById(Long id) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID, USERS.USERNAME)
                .from(LINEUP)
                .join(USERS).on(LINEUP.USER_ID.eq(USERS.ID))
                .where(LINEUP.ID.eq(id))
                .fetchOptional()
                .map(mapping(LineupWithAuthorDTO::new));
    }

    public void updateLineup(Lineup lineup) {
        dsl.fetchOptional(LINEUP, LINEUP.ID.eq(lineup.id()))
                .ifPresent(r -> {
                    r.setId(lineup.id());
                    r.setTitle(lineup.title());
                    r.setBody(lineup.body());
                    r.setAgent(lineup.agent());
                    r.setMap(lineup.map());
                    r.store();
                });
    }

    public void deleteLineup(Long id) {
        boolean exists = dsl.fetchExists(
                selectFrom(LINEUP)
                        .where(LINEUP.ID.eq(id))
        );

        if (!exists) {
            throw new InvalidLineupException.NoSuchLineupException(id);
        }

        dsl.deleteFrom(LINEUP).where(LINEUP.ID.eq(id)).execute();
    }

    // fetches all the lineups from a given user
    public Optional<List<LineupWithAuthorDTO>> getLineupsByUserId(Long userId, Long pageSize, Long lastValue) {
        boolean exists = doesUserExist(userId, false);

        if (!exists) {
            throw new InvalidLineupException.NoUserException(userId);
        }

        if (lastValue != null) {
            List<LineupWithAuthorDTO> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID, USERS.USERNAME)
                    .from(LINEUP)
                    .join(USERS).on(LINEUP.USER_ID.eq(USERS.ID))
                    .where(LINEUP.USER_ID.eq(userId))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetch()
                    .map(mapping(LineupWithAuthorDTO::new));

            return Optional.of(lineups);
        }

        List<LineupWithAuthorDTO> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID, USERS.USERNAME)
                .from(LINEUP)
                .join(USERS).on(LINEUP.USER_ID.eq(USERS.ID))
                .where(LINEUP.USER_ID.eq(userId))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetch()
                .map(mapping(LineupWithAuthorDTO::new));

        return Optional.of(lineups);
    }

    protected boolean doesLineupExist(Long lineupId) {
        // should I also check if the userId is correct? new method?
        boolean exits = dsl.fetchExists(
                selectFrom(LINEUP).where(LINEUP.ID.eq(lineupId))
        );

        if (!exits) {
            throw new InvalidLineupException.NoSuchLineupException(lineupId);
        }
        return true;
    }

    // TODO: Set a limit on pageSize
    public List<LineupWithAuthorDTO> getLineups(String title, Agent agent, Map map, Long pageSize, Long lastValue) {
        var baseQuery = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID, USERS.USERNAME)
                .from(LINEUP)
                .join(USERS).on(LINEUP.USER_ID.eq(USERS.ID));


        Condition conditions = DSL.noCondition();
        if (title != null) {
            conditions = conditions.and(LINEUP.TITLE.eq(title));
        }
        if (agent != null) {
            conditions = conditions.and(LINEUP.AGENT.eq(agent));
        }
        if (map != null) {
            conditions = conditions.and(LINEUP.MAP.eq(map));
        }

        if (lastValue != null) {
            return baseQuery
                    .where(conditions)
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(LineupWithAuthorDTO.class);
        }

        return baseQuery
                .where(conditions)
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(LineupWithAuthorDTO.class);
    }
}