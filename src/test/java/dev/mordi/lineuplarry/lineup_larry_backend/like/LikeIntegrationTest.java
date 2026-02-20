package dev.mordi.lineuplarry.lineup_larry_backend.like;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestRestTemplate
public class LikeIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static HttpHeaders headers;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @BeforeAll
    static void initHeaders() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    // test getAll endpoint
    @Test
    void successGetAllLikes() {
        ResponseEntity<List<Like>> res = restTemplate.exchange("/api/likes", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Like>>() {
                });

        List<Like> expectedArray = List.of(new Like(1L, 2L, null), new Like(1L, 3L, null),
                new Like(1L, 11L, null), new Like(1L, 22L, null), new Like(1L, 18L, null),
                new Like(1L, 16L, null), new Like(2L, 2L, null), new Like(2L, 1L, null),
                new Like(2L, 22L, null), new Like(2L, 23L, null), new Like(2L, 12L, null),
                new Like(2L, 14L, null), new Like(3L, 4L, null), new Like(3L, 22L, null),
                new Like(3L, 15L, null), new Like(3L, 9L, null), new Like(3L, 20L, null),
                new Like(3L, 1L, null), new Like(2L, 20L, null), new Like(4L, 9L, null),
                new Like(4L, 22L, null));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .isEqualTo(expectedArray);
    }

    // test liking a lineup
    @Test
    void successfullyLikeLineup() {
        Like like = new Like(2L, 5L, null);

        ResponseEntity<Like> res = restTemplate.postForEntity("/api/likes",
                new HttpEntity<>(like, headers), // No need to send Like in the body, passing
                                                 // headers
                Like.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().createdAt()).isNotNull();
    }

    // like an already liked lineup
    // we expect a 200 ok and no changes to be applies
    @Test
    void likeAnAlreadyLikedLineup() {
        // confirm that it already exists
        ResponseEntity<Optional<Like>> res = restTemplate.exchange("/api/likes/user/2/lineup/2",
                HttpMethod.GET, null, new ParameterizedTypeReference<Optional<Like>>() {
                });

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isPresent();

        // redo the like
        Like like = new Like(2L, 2L, null);

        ResponseEntity<Like> res2 = restTemplate.postForEntity("/api/likes",
                new HttpEntity<>(like, headers), // No need to send Like in the body, passing
                                                 // headers
                Like.class);
        // confirm that it still persists, again.
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res2.getBody()).isNotNull();
        assertThat(res2.getBody().createdAt()).isNotNull();
        assertThat(res2.getBody()).isEqualTo(res.getBody().get());
    }

    // test getting a like by id
    @Test
    void getNonexistentLikeById() {
        ResponseEntity<String> res = restTemplate.exchange("/api/likes/user/2/lineup/999",
                HttpMethod.GET, null, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("Like not found");
        assertThat(res.getBody()).contains("LIKE_NOT_FOUND");
    }

    // test removing a like
    @Test
    void removeLike() {
        Like likeToDelete = new Like(2L, 2L, null);

        ResponseEntity<String> res = restTemplate.exchange("/api/likes/2", HttpMethod.DELETE,
                new HttpEntity<>(likeToDelete, headers), String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void removeNonexistentLike() {
        Like nonexistendLike = new Like(2L, 999L, null);
        ResponseEntity<String> res = restTemplate.exchange("/api/likes/999", HttpMethod.DELETE,
                new HttpEntity<>(nonexistendLike, headers), String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("Like not found");
        assertThat(res.getBody())
                .contains("The like between userId: '2' and lineupId '999' does not exist");
    }

    @Test
    void successfulGetLikesByUser() {
        ResponseEntity<List<Like>> res = restTemplate.exchange("/api/likes/user/2", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Like>>() {
                });

        List<Like> expectedListOfLikes = List.of(new Like(2L, 2L, null), new Like(2L, 1L, null),
                new Like(2L, 22L, null), new Like(2L, 23L, null), new Like(2L, 12L, null),
                new Like(2L, 14L, null), new Like(2L, 20L, null));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedListOfLikes);
    }

    @Test
    void getLikesOnNonexistentUser() {
        ResponseEntity<String> res = restTemplate.exchange("/api/likes/user/999", HttpMethod.GET,
                null, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("User not found");
        assertThat(res.getBody()).contains("User with id: '999' was not found");
    }

    @Test
    void successfulGetLikesByLineup() {
        ResponseEntity<List<Like>> res = restTemplate.exchange("/api/likes/lineup/2",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Like>>() {
                });

        List<Like> expectedLikes = List.of(new Like(1L, 2L, null), new Like(2L, 2L, null));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedLikes);
    }

    @Test
    void getLikesByLineupOnNonexistentLineup() {
        ResponseEntity<String> res = restTemplate.exchange("/api/likes/lineup/999", HttpMethod.GET,
                null, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("Lineup not found");
        assertThat(res.getBody()).contains("No lineup with id: '999' exists");
    }

    @Test
    void successfulGetLikeCountByLineup() {
        ResponseEntity<Long> res = restTemplate.exchange("/api/likes/lineup/2/count",
                HttpMethod.GET, null, Long.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(2);
    }

    @Test
    void getLikeCountByLineupOnNonexistentLineup() {
        ResponseEntity<String> res = restTemplate.exchange("/api/likes/lineup/999/count",
                HttpMethod.GET, null, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).contains("Lineup not found");
        assertThat(res.getBody()).contains("No lineup with id: '999' exists");
    }
}
