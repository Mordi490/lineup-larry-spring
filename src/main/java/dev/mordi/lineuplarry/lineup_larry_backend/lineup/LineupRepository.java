package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.LINEUP;


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

    public Lineup getLineupById(Long id) {
        return new Lineup(1L, "faux lineup", "faux lineup", id);
    }

    public Lineup updateLineup(Long id, Lineup lineup) {
        return new Lineup(1L, lineup.title(), lineup.body(), id);
    }

    public Lineup deleteLineup(Long id) {
        return new Lineup(1L, "hehe delete", "hehe delete", id);
    }

    // fetches all the lineups from a given user
    public List<Lineup> getLineupsByUserId(Long id) {
        return dsl.select(LINEUP.ID,LINEUP.TITLE,LINEUP.BODY,LINEUP.USER_ID)
                .from(LINEUP)
                .where(LINEUP.USER_ID.eq(id))
                .fetch(r -> new Lineup(r.get(LINEUP.ID), r.get(LINEUP.TITLE), r.get(LINEUP.BODY), r.get(LINEUP.USER_ID)));
    }
}