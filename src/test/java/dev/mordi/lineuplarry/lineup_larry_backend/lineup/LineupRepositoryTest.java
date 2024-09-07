package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JooqTest
@Import({LineupRepository.class})
@Sql("/test-data.sql")
@Testcontainers
public class LineupRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16.3-alpine"
    );

    @Autowired
    LineupRepository lineupRepository;

    // getAll
    @Test
    void getAllLineups() {
        List<Lineup> allLineups = lineupRepository.findAllLineups();

        assertThat(allLineups.size()).isEqualTo(6);
        assertThat(allLineups.getFirst().id()).isEqualTo(1L);
        assertThat(allLineups.getFirst().title()).isEqualTo("lineupOne");
        assertThat(allLineups.getFirst().body()).isEqualTo("bodyOne");
        assertThat(allLineups.getFirst().userId()).isEqualTo(1L);
    }

    // Get by ID
    @Test
    void successfulGetById() throws Exception {
        Optional<Lineup> lineup = lineupRepository.getLineupById(1L);

        if (lineup.isEmpty()) {
            throw new Exception("Failed to fetch lineup with id 1");
        }

        assertThat(lineup.get().id()).isEqualTo(1L);
        assertThat(lineup.get().title()).isEqualTo("lineupOne");
        assertThat(lineup.get().body()).isEqualTo("bodyOne");
        assertThat(lineup.get().userId()).isEqualTo(1L);
    }

    @Test
    void failToFetchNonExistentLineup() {
        Optional<Lineup> lineup = lineupRepository.getLineupById(44L);

        assertThat(lineup).isEmpty();
    }

    // Create
    @Test
    void successfulCreationOfLineup() {
        Lineup newLineup = new Lineup(null, Agent.SOVA, Map.ASCENT, "newly created lineup", "newly created lineup body", 2L);

        Lineup res = lineupRepository.createLineup(newLineup);

        assertThat(res.id()).isNotNull();
        assertThat(res.title()).isEqualTo(newLineup.title());
        assertThat(res.body()).isEqualTo(newLineup.body());
        assertThat(res.userId()).isEqualTo(newLineup.userId());
    }

    @Test
    void failCreateDueToUsingNonexistentUserId() {
        Lineup lineupWithNonexistentUserId = new Lineup(null, Agent.SOVA, Map.ASCENT, "valid title", "valid body", 999L);

        // should be something like this
        assertThrows(InvalidLineupException.UserIdInvalidException.class, () -> {
            lineupRepository.createLineup(lineupWithNonexistentUserId);
        });
    }

    // TODO: revisit this once security has been
    // ie. fail because the user's provided principal does not match with the set userId

    
    // Update
    // success
    @Test
    void successfulUpdate() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup newLineupData = new Lineup(lineupToFetch.get().id(), Agent.KILLJOY, Map.SUNSET, "updated title", "updated body", lineupToFetch.get().userId());
        lineupRepository.updateLineup(newLineupData);

        Optional<Lineup> updatedLineup = lineupRepository.getLineupById(1L);

        if (updatedLineup.isEmpty()) {
            throw new RuntimeException("Failed to fetch updated user");
        }

        assertThat(updatedLineup.get()).isEqualTo(newLineupData);
        assertThat(updatedLineup.get().toString()).isEqualTo(newLineupData.toString());
    }

    // delete
    // TODO: revisit this when security has been added
    @Test
    void successfulDelete() {
        // assure that the user exists
        Optional<Lineup> lineupToDelete = lineupRepository.getLineupById(1L);

        if (lineupToDelete.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }

        assertThat(lineupToDelete.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToDelete.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToDelete.get().id()).isEqualTo(1L);
        assertThat(lineupToDelete.get().userId()).isEqualTo(1L);

        lineupRepository.deleteLineup(1L);

        // extra check, confirm that we cannot fetch recently deleted lineup
        Optional<Lineup> deletedLineup = lineupRepository.getLineupById(1L);
        assertThat(deletedLineup).isEmpty();
    }

    // deletions on ids, that doesn't have a lineup assigned does noting, and returns nothing
    @Test
    void deleteNonExistentLineup() {
        // check for empty lineup
        Optional<Lineup> nonExistentLineup = lineupRepository.getLineupById(42L);

        assertThat(nonExistentLineup).isEmpty();

        lineupRepository.deleteLineup(42L);
    }

    // get all from user
    @Test
    void successfulGetAllLineupsFromUser() {
        Optional<List<Lineup>> lineupsFromUser = lineupRepository.getLineupsByUserId(2L);

        assertThat(lineupsFromUser).isPresent();
        assertThat(lineupsFromUser.get().size()).isEqualTo(2);
        assertThat(lineupsFromUser.get().getFirst().title()).isEqualTo("lineupTwo");
        assertThat(lineupsFromUser.get().getLast().title()).isEqualTo("lineupThree");
    }

    @Test
    void successfulGetAllLineupsFromUserWithZeroLineups() {
        Optional<List<Lineup>> lineupsFromUser = lineupRepository.getLineupsByUserId(4L);

        assertThat(lineupsFromUser).isPresent();
        assertThat(lineupsFromUser.get().size()).isZero();
    }

    @Test
    void getAllLineupsFromNonexistentUser() {
        // expects null which then gets translated into not found?
        Long nonexistentUserId = 55L;
        assertThrows(InvalidLineupException.NoUserException.class, () -> {
            lineupRepository.getLineupsByUserId(nonexistentUserId);
        });
    }

    // testing "getByTitle", lineupId: 4 & 5 share the same title, 'same name'.
    @Test
    void successfulGetByTitle() {
        Optional<List<Lineup>> lineups = lineupRepository.getByTitle("same name");

        assertThat(lineups).isPresent();
        assertThat(lineups.get().size()).isEqualTo(2);
    }

    // return for zero finds
    @Test
    void successfulGetByTitleNoMatches() {
        Optional<List<Lineup>> lineups = lineupRepository.getByTitle("this title will most definitely not result in any lineups being fetched");

        assertThat(lineups).isEmpty();
    }
}
