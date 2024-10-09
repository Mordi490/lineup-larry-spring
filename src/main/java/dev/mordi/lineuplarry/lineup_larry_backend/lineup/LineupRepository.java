package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import org.jooq.DSLContext;
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
    public List<Lineup> findAllLineups() {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .fetch()
                .map(mapping(Lineup::new));
    }

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

    public Optional<Lineup> getLineupById(Long id) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.ID.eq(id))
                .fetchOptional()
                .map(mapping(Lineup::new));
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
        dsl.deleteFrom(LINEUP).where(LINEUP.ID.eq(id)).execute();
    }

    // fetches all the lineups from a given user
    public Optional<List<Lineup>> getLineupsByUserId(Long userId, Long pageSize, Long lastValue) {
        boolean exists = doesUserExist(userId, false);

        if (!exists) {
            throw new InvalidLineupException.NoUserException(userId);
        }

        if (lastValue != null) {
            List<Lineup> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.USER_ID.eq(userId))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetch()
                    .map(mapping(Lineup::new));

            return Optional.of(lineups);
        }

        List<Lineup> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(userId))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetch()
                .map(mapping(Lineup::new));

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

    // there should be some fuzzy search on titles and authorNames
    public List<Lineup> getByTitle(String name, Long pageSize, Long lastValue) {
        if (lastValue != null) {

            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.TITLE.eq(name))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.TITLE.eq(name))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndTitle(Agent agent, String title, Long pageSize, Long lastValue) {
        if (lastValue != null) {
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.AGENT.eq(agent).and(LINEUP.TITLE.eq(title)))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(agent).and(LINEUP.TITLE.eq(title)))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMapAndTitle(Map map, String title, Long pageSize, Long lastValue) {
        if (lastValue != null) {
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.MAP.eq(map).and(LINEUP.TITLE.eq(title)))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(map).and(LINEUP.TITLE.eq(title)))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMap(Map map, Long pageSize, Long lastValue) {
        if (lastValue != null) {
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.MAP.eq(map))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(map))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMap(Agent agent, Map map, Long pageSize, Long lastValue) {
        if (lastValue != null) {
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.MAP.eq(map)).and(LINEUP.AGENT.eq(agent))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(map)).and(LINEUP.AGENT.eq(agent))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMapAndTitle(Agent agent, Map map, String title, Long pageSize, Long lastValue) {
        if (lastValue != null) {
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.MAP.eq(map)).and(LINEUP.AGENT.eq(agent).and(LINEUP.TITLE.eq(title)))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(map)).and(LINEUP.AGENT.eq(agent).and(LINEUP.TITLE.eq(title)))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgent(Agent agent, Long pageSize, Long lastValue) {
        if (lastValue != null) {
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .where(LINEUP.AGENT.eq(agent))
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(agent))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    // calling them getAllLineupsByAgentMorphed, just to make it easier to ensure feature parity later
    public List<Lineup> getLineups(Long pageSize, Long lastValue) {
        if (lastValue != null) {
            // might have to check if the last value is valid if we get an empty list here
            return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                    .from(LINEUP)
                    .orderBy(LINEUP.ID.asc())
                    .seek(lastValue)
                    .limit(pageSize)
                    .fetchInto(Lineup.class);
        }
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }
}