package dev.mordi.lineuplarry.lineup_larry_backend.like;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.mordi.lineuplarry.lineup_larry_backend.like.exceptions.InvalidLikeException;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JooqTest
@Import({LikeRepository.class})
@Sql("/test-data.sql")
@Testcontainers
public class LikeRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    LikeRepository likeRepository;

    // test getting likes
    @Test
    void getAllLikes() {
        List<Like> res = likeRepository.getAllLikes();

        assertThat(res).isNotEmpty();
        assertThat(res.toArray().length).isEqualTo(21);
        assertThat(res.toArray()[0])
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(new Like(1L, 2L, OffsetDateTime.now()));

        assertThat(res.toArray()[17])
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(new Like(3L, 1L, OffsetDateTime.now()));
    }

    // test creation of likes
    @Test
    void likeLineup() {
        Like likeToSend = new Like(2L, 5L, null);

        var res = likeRepository.likeLineup(likeToSend);

        assertThat(res)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(new Like(2L, 5L, OffsetDateTime.now()));
        assertThat(res.createdAt()).isNotNull();
    }

    // like an already liked lineup
    @Test
    void likeAnAlreadyLikedLineup() {
        // (2, 2) is already in the seed data, ie already liked
        Like like = new Like(2L, 2L, null);

        var res = likeRepository.likeLineup(like);

        assertThat(res)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(new Like(2L, 2L, OffsetDateTime.now()));
        assertThat(res.createdAt()).isNotNull();
    }

    @Test
    void failToCreateLikeOnInvalidLineupId() {
        Like likeToFail = new Like(2L, 999L, null);

        assertThrows(InvalidLineupException.NoSuchLineupException.class, () -> {
            likeRepository.likeLineup(likeToFail);
        });
    }

    // test removal of likes
    @Test
    void removeLike() {
        // confirm that like exists
        var like = likeRepository.getLikeById(2L, 2L);
        assertThat(like).isPresent();
        assertThat(like.get().lineupId()).isNotNull();
        // delete the like
        Like likeToRemove = new Like(2L, 2L, null);
        likeRepository.removeLike(likeToRemove);
        // confirm that the like has been deleted
        var deletedLike = likeRepository.getLikeById(2L, 2L);
        assertThat(deletedLike).isEmpty();
    }

    @Test
    void failToRemoveNonexistentLike() {
        Like nonExistentLike = new Like(2L, 999L, null);

        assertThrows(InvalidLikeException.LikeNotFound.class, () -> {
            likeRepository.removeLike(nonExistentLike);
        });
    }

    // getLikesByUser
    @Test
    void successfulLikesByUser() {
        List<Like> userTwosLikes = likeRepository.getLikesByUser(2L);

        List<Like> expectedLikesList = List.of(
                new Like(2L, 2L, null),
                new Like(2L, 1L, null),
                new Like(2L, 22L, null),
                new Like(2L, 23L, null),
                new Like(2L, 12L, null),
                new Like(2L, 14L, null),
                new Like(2L, 20L, null));

        assertThat(userTwosLikes)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedLikesList);
    }

    @Test
    void getLikesFromNonexistentUser() {
        assertThrows(InvalidUserException.UserNotFoundException.class, () -> {
            likeRepository.getLikesByUser(999L);
        });
    }

    // getLikesByLineup
    @Test
    void successfulGetLikesByLineup() {
        List<Like> likesForLineupTwo = likeRepository.getLikesByLineup(2L);

        List<Like> expectedLikes = List.of(new Like(1L, 2L, null), new Like(2L, 2L, null));

        assertThat(likesForLineupTwo)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedLikes);
    }

    @Test
    void getLikesByLineupOnNonexistentLineup() {
        assertThrows(InvalidLineupException.NoSuchLineupException.class, () -> {
            likeRepository.getLikeCountByLineup(999L);
        });
    }

    // getLikeCountByLineup
    @Test
    void successfulGetLikeCountByLineup() {
        long likeCountOfLineupTwo = likeRepository.getLikeCountByLineup(2L);

        long expectedLikeCount = 2L;

        assertThat(likeCountOfLineupTwo).isEqualTo(expectedLikeCount);
    }

    @Test
    void getLikeCountOnNonexistentLineup() {
        assertThrows(InvalidLineupException.NoSuchLineupException.class, () -> {
            likeRepository.getLikeCountByLineup(999L);
        });
    }

    @Test
    void getLikeCountByLineupOnNonexistentLineup() {
        assertThrows(InvalidLineupException.NoSuchLineupException.class, () -> {
            likeRepository.getLikeCountByLineup(999L);
        });
    }
}
