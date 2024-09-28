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


@Repository
public class LineupRepository {

    private final DSLContext dsl;

    LineupRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

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

    private boolean doesUserExist(Long userId, boolean isCreate) {
        boolean exists = dsl.fetchExists(
                dsl.selectOne().from(USERS).where(USERS.ID.eq(userId))
        );
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
    public Optional<List<Lineup>> getLineupsByUserId(Long id) {
        boolean exists = doesUserExist(id, false);

        if (!exists) {
            throw new InvalidLineupException.NoUserException(id);
        }

        List<Lineup> lineups = dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(id))
                .fetch()
                .map(mapping(Lineup::new));

        return Optional.of(lineups);
    }

    // there should be some fuzzy search on titles and authorNames
    public List<Lineup> getByTitle(String name) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.TITLE.eq(name))
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgent(Agent validatedAgent) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(validatedAgent))
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndTitle(Agent validatedAgent, String title) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.AGENT.eq(validatedAgent).and(LINEUP.TITLE.eq(title)))
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMapAndTitle(Map validatedMap, String title) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap).and(LINEUP.TITLE.eq(title)))
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByMap(Map validatedMap) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap))
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMap(Agent validatedAgent, Map validatedMap) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap)).and(LINEUP.AGENT.eq(validatedAgent))
                .fetchInto(Lineup.class);
    }

    public List<Lineup> findByAgentAndMapAndTitle(Agent validatedAgent, Map validatedMap, String title) {
        return dsl.select(LINEUP.ID, LINEUP.AGENT, LINEUP.MAP, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.MAP.eq(validatedMap)).and(LINEUP.AGENT.eq(validatedAgent).and(LINEUP.TITLE.eq(title)))
                .fetchInto(Lineup.class);
    }
}