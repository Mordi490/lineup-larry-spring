package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JooqTest
@Import({LineupRepository.class})
@Sql("/test-data.sql")
@Testcontainers
public class LineupRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    LineupRepository lineupRepository;

    // Get by ID
    @Test
    void successfulGetById() throws Exception {
        Optional<LineupWithAuthorDTO> lineup = lineupRepository.getLineupById(1L);

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
        Optional<LineupWithAuthorDTO> lineup = lineupRepository.getLineupById(44L);

        assertThat(lineup).isEmpty();
    }

    // Create
    @Test
    void successfulCreationOfLineup() {
        Lineup newLineup = new Lineup(null, Agent.SOVA, Map.ASCENT, "newly created lineup",
                "newly created lineup body", 2L, null, null);

        Lineup res = lineupRepository.createLineup(newLineup);

        assertThat(res.id()).isNotNull();
        assertThat(res.title()).isEqualTo(newLineup.title());
        assertThat(res.body()).isEqualTo(newLineup.body());
        assertThat(res.userId()).isEqualTo(newLineup.userId());
        assertThat(res.createdAt()).isNotNull();
        assertThat(res.updatedAt()).isNotNull();
    }

    @Test
    void failCreateDueToUsingNonexistentUserId() {
        Lineup lineupWithNonexistentUserId = new Lineup(null, Agent.SOVA, Map.ASCENT, "valid title",
                "valid body", 999L, null, null);

        assertThrows(InvalidLineupException.UserIdInvalidException.class, () -> {
            lineupRepository.createLineup(lineupWithNonexistentUserId);
        });
    }

    // TODO: revisit this once security has been
    // ie. fail because the user's provided principal does not match with the set
    // userId

    // Update
    // success
    @Test
    void successfulUpdate() {
        Optional<LineupWithAuthorDTO> lineupToFetch = lineupRepository.getLineupById(1L);

        if (lineupToFetch.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }
        assertThat(lineupToFetch.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToFetch.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToFetch.get().id()).isEqualTo(1L);
        assertThat(lineupToFetch.get().userId()).isEqualTo(1L);

        Lineup newLineupData = new Lineup(lineupToFetch.get().id(), Agent.KILLJOY, Map.SUNSET,
                "updated title", "updated body", lineupToFetch.get().userId(), null, null);
        lineupRepository.updateLineup(newLineupData);

        Optional<LineupWithAuthorDTO> updatedLineup = lineupRepository.getLineupById(1L);

        if (updatedLineup.isEmpty()) {
            throw new RuntimeException("Failed to fetch updated user");
        }

        assertThat(updatedLineup.get()).usingRecursiveComparison()
                .ignoringFields("authorUsername", "createdAt", "updatedAt")
                .isEqualTo(newLineupData);
    }

    // delete
    // TODO: revisit this when security has been added
    @Test
    void successfulDelete() {
        // assure that the user exists
        Optional<LineupWithAuthorDTO> lineupToDelete = lineupRepository.getLineupById(1L);

        if (lineupToDelete.isEmpty()) {
            throw new RuntimeException("Failed to fetch lineup with Id 1");
        }

        assertThat(lineupToDelete.get().title()).isEqualTo("lineupOne");
        assertThat(lineupToDelete.get().body()).isEqualTo("bodyOne");
        assertThat(lineupToDelete.get().id()).isEqualTo(1L);
        assertThat(lineupToDelete.get().userId()).isEqualTo(1L);

        lineupRepository.deleteLineup(1L);

        // extra check, confirm that we cannot fetch recently deleted lineup
        Optional<LineupWithAuthorDTO> deletedLineup = lineupRepository.getLineupById(1L);
        assertThat(deletedLineup).isEmpty();
    }

    // should return an error, which maps to 404 not found
    @Test
    void deleteNonExistentLineup() {
        // check for empty lineup
        Optional<LineupWithAuthorDTO> nonExistentLineup = lineupRepository.getLineupById(42L);

        assertThat(nonExistentLineup).isEmpty();
        assertThrows(InvalidLineupException.NoSuchLineupException.class, () -> {
            lineupRepository.deleteLineup(42L);
        });
    }

    // get all from user
    @Test
    void successfulGetAllLineupsFromUser() {
        Optional<List<LineupWithAuthorDTO>> lineupsFromUser = lineupRepository
                .getLineupsByUserId(2L, 20L, null);

        assertThat(lineupsFromUser).isPresent();
        assertThat(lineupsFromUser.get().size()).isEqualTo(3);
        assertThat(lineupsFromUser.get().getFirst().title()).isEqualTo("lineupTwo");
        assertThat(lineupsFromUser.get().getLast().title()).isEqualTo("teleport thingy");
    }

    @Test
    void successfulGetAllLineupsFromUserWithZeroLineups() {
        Optional<List<LineupWithAuthorDTO>> lineupsFromUser = lineupRepository
                .getLineupsByUserId(4L, 20L, null);

        assertThat(lineupsFromUser).isPresent();
        assertThat(lineupsFromUser.get().size()).isZero();
    }

    @Test
    void getAllLineupsFromNonexistentUser() {
        // expects null which then gets translated into not found?
        Long nonexistentUserId = 55L;
        assertThrows(InvalidLineupException.NoUserException.class, () -> {
            lineupRepository.getLineupsByUserId(nonexistentUserId, 20L, null);
        });
    }

    @Test
    void successfulGetAllLineupsFromUserPaginated() {
        Optional<List<LineupWithAuthorDTO>> lineups = lineupRepository.getLineupsByUserId(2L, 20L,
                2L);

        List<LineupWithAuthorDTO> expectedLineups = List.of(
                new LineupWithAuthorDTO(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree", "bodyThree",
                        2L, null, null, "userTwo"),
                new LineupWithAuthorDTO(9L, Agent.YORU, Map.HAVEN, "teleport thingy",
                        "good for post plant", 2L, null, null, "userTwo"));

        assertThat(lineups).isPresent();
        assertThat(lineups.get()).isNotNull();
        assertThat(lineups.get()).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt").isEqualTo(expectedLineups);
    }

    // test on nonexistent user, which should return an error
    @Test
    void GetAllLineupsFromNonexistentUserPaginated() {
        Long nonexistentUserId = 999L;
        assertThrows(InvalidLineupException.NoUserException.class, () -> {
            lineupRepository.getLineupsByUserId(nonexistentUserId, 20L, 2L);
        });
    }

    // test on invalid lastValue, which is just empty set
    @Test
    void getAllLineupsFromUserWithInvalidLastValue() {
        Optional<List<LineupWithAuthorDTO>> lineups = lineupRepository.getLineupsByUserId(2L, 20L,
                333L);

        assertThat(lineups).isPresent();
        assertThat(lineups.get()).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    void successfulFindByMapAndTitle() {
        List<LineupWithAuthorDTO> query = lineupRepository.getLineups("same name", null, Map.ICEBOX,
                20L, null);

        List<LineupWithAuthorDTO> expectedResult = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(query)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    // pagination, success

    @Test
    void findByMapAndTitlePagination() {
        List<LineupWithAuthorDTO> query = lineupRepository.getLineups("same name", null, Map.ICEBOX,
                20L, 5L);

        List<LineupWithAuthorDTO> expectedResult = List.of(new LineupWithAuthorDTO(6L,
                Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L, null, null, "userThree"));

        assertThat(query)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    // no matches, just agent, vs just map vs both
    @Test
    void emptyFindByMapAndTitle() {
        List<LineupWithAuthorDTO> query = lineupRepository.getLineups("not a match", null,
                Map.PEARL, 20L, null);

        List<LineupWithAuthorDTO> expectedList = List.of();

        assertThat(query).isEqualTo(expectedList);
    }

    // testing "getByTitle", lineupId: 4 & 5 share the same title, 'same name'.
    @Test
    void successfulGetByTitle() {
        List<LineupWithAuthorDTO> lineups = lineupRepository.getLineups("same name", null, null,
                20L, null);

        List<LineupWithAuthorDTO> expectedResult = List.of(
                new LineupWithAuthorDTO(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"),
                new LineupWithAuthorDTO(6L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L,
                        null, null, "userThree"));

        assertThat(lineups).isNotEmpty();
        assertThat(lineups.size()).isEqualTo(2);
        assertThat(lineups.stream().toList())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    @Test
    void successfulGetByTitlePageSized() {
        List<LineupWithAuthorDTO> lineups = lineupRepository.getLineups("same name", null, null, 1L,
                null);

        List<LineupWithAuthorDTO> expectedResult = List.of(new LineupWithAuthorDTO(5L,
                Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L, null, null, "userThree"));

        assertThat(lineups).isNotEmpty();
        assertThat(lineups.size()).isEqualTo(1);
        assertThat(lineups.stream().toList())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    @Test
    void successfulGetByTitlePageSizedPagination() {
        List<LineupWithAuthorDTO> lineups = lineupRepository.getLineups("same name", null, null, 1L,
                5L);

        List<LineupWithAuthorDTO> expectedResult = List.of(new LineupWithAuthorDTO(6L,
                Agent.KILLJOY, Map.ICEBOX, "same name", "bodyFour", 3L, null, null, "userThree"));

        assertThat(lineups).isNotEmpty();
        assertThat(lineups.size()).isEqualTo(1);
        assertThat(lineups.stream().toList())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedResult);
    }

    // return for zero finds
    @Test
    void successfulGetByTitleNoMatches() {
        List<LineupWithAuthorDTO> lineups = lineupRepository.getLineups(
                "this title will most definitely not result in any lineups being fetched", null,
                null, 20L, null);

        assertThat(lineups).isEmpty();
    }

    @Test
    void successfulGetByAgentMapAndTitle() {
        List<LineupWithAuthorDTO> lineups = lineupRepository.getLineups("lineupThree",
                Agent.BRIMSTONE, Map.BIND, 20L, null);

        List<LineupWithAuthorDTO> expectedLineup = Collections
                .singletonList(new LineupWithAuthorDTO(3L, Agent.BRIMSTONE, Map.BIND, "lineupThree",
                        "bodyThree", 2L, null, null, "userTwo"));

        assertThat(lineups).isNotEmpty();
        assertThat(lineups)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedLineup);
    }

    @Test
    void successfulDoesUserExistOnExistingUser() {
        boolean answer = lineupRepository.doesUserExist(1L, false);

        assertThat(answer).isEqualTo(true);
    }

    @Test
    void negativeDoesUserExistOnNonexistentId() {
        Long nonexistentUserId = 999L;
        assertThrows(InvalidLineupException.NoUserException.class, () -> {
            lineupRepository.doesUserExist(nonexistentUserId, false);
        });
    }

    @Test
    void existingDoesUserExistIsCreate() {
        boolean answer = lineupRepository.doesUserExist(1L, true);

        assertThat(answer).isEqualTo(true);
    }

    @Test
    void nonexistentDoesUserExistIsCreate() {
        Long nonexistentUserId = 999L;
        assertThrows(InvalidLineupException.UserIdInvalidException.class, () -> {
            lineupRepository.doesUserExist(nonexistentUserId, true);
        });
    }

    @Test
    void successfulDoesLineupExist() {
        Long validLineupId = 2L;
        boolean res = lineupRepository.doesLineupExist(validLineupId);

        assertThat(res).isEqualTo(true);
    }

    @Test
    void failOnNonexistentLineupId() {
        long nonexistentLineupId = 999L;
        assertThrows(InvalidLineupException.NoSuchLineupException.class, () -> {
            lineupRepository.doesLineupExist(nonexistentLineupId);
        });
    }
}
