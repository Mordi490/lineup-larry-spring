package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Test
    void dbHasBeenPopulated() {
        List<Lineup> allLineups = lineupRepository.findAllLineups();
        assertThat(allLineups.size()).isEqualTo(4);
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

        assertThat(lineup.isEmpty());
    }

    // Create
    @Test
    void successfulCreationOfLineup() {
        Lineup newLineup = new Lineup(null, "newly created lineup", "newly created lineup body", 2L);

        Lineup res = lineupRepository.createLineup(newLineup);

        assertThat(res.id()).isNotNull();
        assertThat(res.title()).isEqualTo(newLineup.title());
        assertThat(res.body()).isEqualTo(newLineup.body());
        assertThat(res.userId()).isEqualTo(newLineup.userId());
    }

    // fail creation with empty and/or blank title and/or body
    @Test
    void failCreationForBlankTitle() {
        Lineup lineupWithBlankTitle = new Lineup(null, "  ", "not blank body", 2L);

        assertThrows(InvalidLineupException.BlankTitleException.class, () -> {
            lineupRepository.createLineup(lineupWithBlankTitle);
        });
    }

    @Test
    void failCreationForBlankBody() {
        Lineup lineupWithBlankBody = new Lineup(null, "valid title", "   ", 2L);

        assertThrows(InvalidLineupException.BlankBodyException.class, () -> {
            lineupRepository.createLineup(lineupWithBlankBody);
        });
    }

    @Test
    void failCreationForNullTitle() {
        Lineup lineupWithNullTitle = new Lineup(null, null, "valid body", 2L);

        assertThrows(InvalidLineupException.NullTitleException.class, () -> {
            lineupRepository.createLineup(lineupWithNullTitle);
        });
    }

    @Test
    void failCreationForNullBody() {
        Lineup lineupWithNullBody = new Lineup(null, "valid title", null, 2L);

        assertThrows(InvalidLineupException.NullBodyException.class, () -> {
            lineupRepository.createLineup(lineupWithNullBody);
        });
    }

    // fail creation with a "null" as userId
    @Test
    void failCreationForNullUserId() {
        Lineup lineupWitNullUserId = new Lineup(null, "valid title", "valid body", null);

        assertThrows(InvalidLineupException.UserIdNullException.class, () -> {
            lineupRepository.createLineup(lineupWitNullUserId);
        });
    }

    // TODO: revisit this once security has been
    @Test
    void failCreationForMismatchedUserId() {
        Lineup lineupWitNullUserId = new Lineup(242L, "valid title", "valid body", 250L);

        assertThrows(DataIntegrityViolationException.class, () -> {
            lineupRepository.createLineup(lineupWitNullUserId);
        });
    }

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

        Lineup newLineupData = new Lineup(lineupToFetch.get().id(), "updated title", "updated body", lineupToFetch.get().userId());
        lineupRepository.updateLineup(newLineupData);

        Optional<Lineup> updatedLineup = lineupRepository.getLineupById(1L);

        if (updatedLineup.isEmpty()) {
            throw new RuntimeException("Failed to fetch updated user");
        }

        assertThat(updatedLineup.get().title()).isEqualTo("updated title");
        assertThat(updatedLineup.get().body()).isEqualTo("updated body");
        assertThat(updatedLineup.get().id()).isEqualTo(1L);
        assertThat(updatedLineup.get().userId()).isEqualTo(1L);
    }

    // helper method since we always use lineup 1L and do the same assertions
    // ??? code it here bud

    // fail
    // reasonable errors on bad data
    @Test
    void failUpdateWithBlankTitle() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithBlankTitle = lineupToFetch.get().withTitle("  ");
        assertThrows(InvalidLineupException.BlankTitleException.class, () -> {
            lineupRepository.updateLineup(lineupWithBlankTitle);
        });
    }

    @Test
    void failUpdateWithBlankBody() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithBlankBody = lineupToFetch.get().withBody("  ");
        assertThrows(InvalidLineupException.BlankBodyException.class, () -> {
            lineupRepository.updateLineup(lineupWithBlankBody);
        });
    }

    @Test
    void failUpdateWithEmptyTitle() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithEmptyTitle = lineupToFetch.get().withTitle("");
        assertThrows(InvalidLineupException.EmptyTitleException.class, () -> {
            lineupRepository.updateLineup(lineupWithEmptyTitle);
        });
    }

    @Test
    void failUpdateWithEmptyBody() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithEmptyBody = lineupToFetch.get().withBody("");
        assertThrows(InvalidLineupException.EmptyBodyException.class, () -> {
            lineupRepository.updateLineup(lineupWithEmptyBody);
        });
    }

    @Test
    void failUpdateWithNullTitle() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithNullTitle = lineupToFetch.get().withTitle(null);
        assertThrows(InvalidLineupException.NullTitleException.class, () -> {
            lineupRepository.updateLineup(lineupWithNullTitle);
        });
    }

    @Test
    void failUpdateWithNullBody() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithNullBody = lineupToFetch.get().withBody(null);
        assertThrows(InvalidLineupException.NullBodyException.class, () -> {
            lineupRepository.updateLineup(lineupWithNullBody);
        });
    }

    // TODO: update this when security has been added
    @Test
    void failUpdateWithMismatchedUserId() {
        /*
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithMismatchedUserId = lineupToFetch.get().withUserId(2L);
        assertThrows(InvalidLineupException.UserIdInvalidException.class, () -> {
            lineupRepository.updateLineup(lineupWithMismatchedUserId);
        });
         */
    }

    @Test
    void failUpdateWithNullId() {
        Optional<Lineup> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup lineupWithNullId = lineupToFetch.get().withId(null);
        assertThrows(InvalidLineupException.NullLineupIdException.class, () -> {
            lineupRepository.updateLineup(lineupWithNullId);
        });
    }

    // delete
    // success cases
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
    void failSilentlyOnLineupsThatDoNotExist() {
        // check for empty lineup
        Optional<Lineup> nonExistentLineup = lineupRepository.getLineupById(42L);

        assertThat(nonExistentLineup).isEmpty();

        lineupRepository.deleteLineup(42L);
    }

    // failure cases
    // fail to delete on bad data, null, empty strings, etc
    @Test
    void failDeleteForNull() {
        assertThrows(InvalidLineupException.NullLineupIdException.class, () -> {
            lineupRepository.deleteLineup(null);
        });
    }

    // get all from user
    @Test
    void successfulGetAllLineupsFromUser() {
        // confirm that the user has lineups
        List<Lineup> lineupsFromUser = lineupRepository.getLineupsByUserId(2L);

        assertThat(lineupsFromUser.size()).isEqualTo(2);
        assertThat(lineupsFromUser.getFirst().title()).isEqualTo("lineupTwo");
        assertThat(lineupsFromUser.getLast().title()).isEqualTo("lineupThree");
    }

    @Test
    void failGetAllLineupsFromANonExistentUser() {
        List<Lineup> lineupsFromUser = lineupRepository.getLineupsByUserId(42L);

        // make it fail?
        // consider having it set to zero as to not leak internal info such as what id a user has
        assertThat(lineupsFromUser.size()).isZero();
    }

    @Test
    void successfulGetAllLineupsFromUserWithZeroLineups() {
        List<Lineup> lineupsFromUser = lineupRepository.getLineupsByUserId(4L);

        assertThat(lineupsFromUser.size()).isZero();
    }
}
