package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

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
        System.out.println("Received lineup (repo):" + lineup);
        return dsl.insertInto(LINEUP)
                .set(LINEUP.TITLE, lineup.title())
                .set(LINEUP.BODY, lineup.body())
                .set(LINEUP.USER_ID, lineup.userId())
                .returning()
                .fetchOne(record -> new Lineup(record.getId(), record.getTitle(), record.getBody(), record.getUserId()));
    }

    public Optional<Lineup> getLineupById(Long id) {
        return dsl.select(LINEUP.ID, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.ID.eq(id))
                .fetchOptional().map(mapping(Lineup::create));
    }

    public void updateLineup(Lineup lineup) {
        dsl.fetchOptional(LINEUP, LINEUP.ID.eq(lineup.id()))
                .ifPresent(r -> {
                    r.setId(lineup.id());
                    r.setTitle(lineup.title());
                    r.setBody(lineup.body()); // I assume that userId is never going to change
                    r.store();
                });
    }

    public void deleteLineup(Long id) {
        dsl.deleteFrom(LINEUP).where(LINEUP.ID.eq(id)).execute();
    }

    // fetches all the lineups from a given user
    public List<Lineup> getLineupsByUserId(Long id) {
        return dsl.select(LINEUP.ID, LINEUP.TITLE, LINEUP.BODY, LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(id))
                .fetch(r -> new Lineup(r.get(LINEUP.ID), r.get(LINEUP.TITLE), r.get(LINEUP.BODY), r.get(LINEUP.USER_ID)));
    }
}