package dev.mordi.lineuplarry.lineup_larry_backend.like;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

// Look in
// @SpringJUnitConfig(WebConfig.class) // Specify the configuration to load

// Remember to remove once we've migrated to the RestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
@Testcontainers
@AutoConfigureRestTestClient
public class LikeIntegrationTest {

    @Autowired
    RestTestClient client;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    // test getAll endpoint
    @Test
    void successGetAllLikes() {
        List<Like> likes = client.get()
                .uri("/api/likes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Like>>() {
                })
                .returnResult()
                .getResponseBody();

        List<Like> expectedArray = List.of(new Like(1L, 2L, null), new Like(1L, 3L, null),
                new Like(1L, 11L, null), new Like(1L, 22L, null), new Like(1L, 18L, null),
                new Like(1L, 16L, null), new Like(2L, 2L, null), new Like(2L, 1L, null),
                new Like(2L, 22L, null), new Like(2L, 23L, null), new Like(2L, 12L, null),
                new Like(2L, 14L, null), new Like(3L, 4L, null), new Like(3L, 22L, null),
                new Like(3L, 15L, null), new Like(3L, 9L, null), new Like(3L, 20L, null),
                new Like(3L, 1L, null), new Like(2L, 20L, null), new Like(4L, 9L, null),
                new Like(4L, 22L, null));

        assertThat(likes)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
                .isEqualTo(expectedArray);
    }

    // test liking a lineup
    @Test
    void successfullyLikeLineup() {
        Like like = new Like(2L, 5L, null);

        Like responseBody = client.post()
                .uri("/api/likes")
                .body(like)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Like.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody.createdAt()).isNotNull();
    }

    // like an already liked lineup
    // we expect a 200 ok and no changes to be applies
    @Test
    void likeAnAlreadyLikedLineup() {
        // Confirm that the lineup is already liked
        var response = client.get()
                .uri("/api/likes/user/2/lineup/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Like.class)
                .returnResult()
                .getResponseBody();

        Like like = new Like(2L, 2L, null);

        // Like the lineup again, we expect ????
        var response2 = client.post()
                .uri("/api/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(like)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Like.class)
                .returnResult()
                .getResponseBody();

        System.out.println(response);
        System.out.println(response2);
        assertThat(response2.createdAt()).isNotNull();
        assertThat(response2).isEqualTo(response);
    }

    // test getting a like by id
    @Test
    void getNonexistentLikeById() {
        client.get()
                .uri("/api/likes/user/2/lineup/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("Like not found");
                    assertThat(body).contains("LIKE_NOT_FOUND");
                })
                .returnResult()
                .getResponseBody();
    }

    // test removing a like
    @Test
    void removeLike() {
        Like likeToDelete = new Like(2L, 2L, null);

        client.method(HttpMethod.DELETE)
                .uri("/api/likes/2")
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeToDelete)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void removeNonexistentLike() {
        Like nonexistentLike = new Like(2L, 999L, null);

        client.method(HttpMethod.DELETE)
                .uri("/api/likes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .body(nonexistentLike)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                          "status": 404,
                          "title": "Like not found",
                          "code": "LIKE_NOT_FOUND",
                          "detail": "The like between userId: '2' and lineupId '999' does not exist",
                          "instance": "/api/likes/999",
                          "type": "https://lineup-larry.dev/problems/likes/not-found"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulGetLikesByUser() {
        List<Like> likes = client.get()
                .uri("/api/likes/user/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Like>>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(likes).hasSize(7);
        assertThat(likes).extracting(Like::userId).containsOnly(2L);
        assertThat(likes).extracting(Like::lineupId)
                .containsExactly(2L, 1L, 22L, 23L, 12L, 14L, 20L);
    }

    @Test
    void getLikesOnNonexistentUser() {
        client.get()
                .uri("/api/likes/user/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                          "status": 404,
                          "title": "User not found",
                          "code": "USER_NOT_FOUND",
                          "detail": "User with id: '999' was not found",
                          "instance": "/api/likes/user/999",
                          "type": "https://lineup-larry.dev/problems/users/not-found"
                        }
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulGetLikesByLineup() {
        client.get()
                .uri("/api/likes/lineup/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                        [
                        {"userId": 1, "lineupId": 2},
                        {"userId": 2, "lineupId": 2}
                        ]
                        """, JsonCompareMode.LENIENT);
    }

    @Test
    void getLikesByLineupOnNonexistentLineup() {
        client.get()
                .uri("/api/likes/lineup/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json("""
                        {
                        "status": 404,
                        "title": "Lineup not found",
                        "code": "LINEUP_NOT_FOUND",
                        "detail": "No lineup with id: '999' exists",
                        "instance": "/api/likes/lineup/999",
                        "type": "https://lineup-larry.dev/problems/lineups/not-found"
                        }
                            """, JsonCompareMode.LENIENT);
    }

    @Test
    void successfulGetLikeCountByLineup() {
        client.get()
                .uri("/api/likes/lineup/2/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .isEqualTo(2L);
    }

    @Test
    void getLikeCountByLineupOnNonexistentLineup() {
        client.get()
                .uri("/api/likes/lineup/999/count")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json("""
                        {
                        "status": 404,
                        "title": "Lineup not found",
                        "code": "LINEUP_NOT_FOUND",
                        "detail": "No lineup with id: '999' exists",
                        "instance": "/api/likes/lineup/999/count",
                        "type": "https://lineup-larry.dev/problems/lineups/not-found"
                        }
                            """, JsonCompareMode.LENIENT);
    }
}
