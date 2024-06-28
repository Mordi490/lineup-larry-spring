package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

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

import static org.assertj.core.api.Assertions.assertThat;

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
}
