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
    public Optional<List<Lineup>> getLineupsByUserId(Long id, Long pageSize) {
        boolean exists = doesUserExist(id, false);

        if (!exists) {
            throw new InvalidLineupException.NoUserException(id);
        }

        List<Lineup> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(id))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetch()
                .map(mapping(Lineup::new));

        return Optional.of(lineups);
    }

    public Optional<List<Lineup>> getLineupsByUserIdPaginated(Long id, Long pageSize, long lastValue) {
        boolean exists = doesUserExist(id, false);

        if (!exists) {
            throw new InvalidLineupException.NoUserException(id);
        }

        List<Lineup> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(id))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetch()
                .map(mapping(Lineup::new));

        return Optional.of(lineups);
    }

    // there should be some fuzzy search on titles and authorNames
    public List<Lineup> getByTitle(String name, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.TITLE.eq(name))
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> getByTitlePagination(String name, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.TITLE.eq(name))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndTitle(Agent validatedAgent, String title, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(validatedAgent).and(LINEUP.TITLE.eq(title)))
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndTitlePaginated(Agent validatedAgent, String title, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(validatedAgent).and(LINEUP.TITLE.eq(title)))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMapAndTitle(Map validatedMap, String title, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap).and(LINEUP.TITLE.eq(title)))
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMapAndTitlePaginated(Map validatedMap, String title, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap).and(LINEUP.TITLE.eq(title)))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMap(Map validatedMap, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap))
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMapPaginated(Map validatedMap, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMap(Agent validatedAgent, Map validatedMap, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap)).and(LINEUP.AGENT.eq(validatedAgent))
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMapPaginated(Agent validatedAgent, Map validatedMap, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap)).and(LINEUP.AGENT.eq(validatedAgent))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMapAndTitle(Agent validatedAgent, Map validatedMap, String title, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap)).and(LINEUP.AGENT.eq(validatedAgent).and(LINEUP.TITLE.eq(title)))
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMapAndTitlePaginated(Agent validatedAgent, Map validatedMap, String title, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap)).and(LINEUP.AGENT.eq(validatedAgent).and(LINEUP.TITLE.eq(title)))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgent(Agent validatedAgent, Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(validatedAgent))
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentPaginated(Agent agent, Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(agent))
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> getAllLineups(Long pageSize) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .orderBy(LINEUP.ID.asc())
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }

    public List<Lineup> getAllLineupsPaginated(Long pageSize, Long lastValue) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .orderBy(LINEUP.ID.asc())
                .seek(lastValue)
                .limit(pageSize)
                .fetchInto(Lineup.class);
    }
}