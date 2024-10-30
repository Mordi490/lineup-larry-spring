package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.like.Like;
import dev.mordi.lineuplarry.lineup_larry_backend.like.LikeRepository;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.Lineup;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupRepository;
import dev.mordi.lineuplarry.lineup_larry_backend.user.User;
import dev.mordi.lineuplarry.lineup_larry_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Profile("dev")
public class DevModeConfig {

    @Autowired
    UserRepository userRepository;
    // TODO: report that no solid error was given on FF vs brave on cors err
    @Autowired
    LineupRepository lineupRepository;

    @Autowired
    LikeRepository likeRepository;

    @Bean
    public ApplicationRunner devModeRunner(Environment environment) {
        return args -> {
            System.out.println("application has started with 'dev' as active profile");
            System.out.println("starting to seed the application!");

            // Note: try to make this mirror the "test-data.sql" file for consistently

            // add users
            userRepository.createUser(new User(null, "userOne"));
            userRepository.createUser(new User(null, "userTwo"));
            userRepository.createUser(new User(null, "userThree"));
            userRepository.createUser(new User(null, "userFour"));
            userRepository.createUser(new User(null, "userFive"));

            // seeded lineup data: designating REYNA and PEARL to be the agent and map with no data associated with them
            lineupRepository.createLineup(new Lineup(null, Agent.SOVA, Map.ASCENT, "titleOne", "bodyOne", 1L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.SOVA, Map.ASCENT, "titleTwo", "bodyTwo", 2L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.BRIMSTONE, Map.BIND, "titleThree", "bodyThree", 2L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.CYPHER, Map.SUNSET, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.KILLJOY, Map.ICEBOX, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.KILLJOY, Map.ICEBOX, "titleFour", "bodyFour", 3L, null, null));

            lineupRepository.createLineup(new Lineup(null, Agent.CHAMBER, Map.SPLIT, "awp crutch", "filler text here", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.BREACH, Map.FRACTURE, "some flash", "even more filler text here", 1L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.YORU, Map.HAVEN, "teleport thingy", "good for post plant", 2L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.PHOENIX, Map.LOTUS, "cheeky flash", " then click heads", 1L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.SKYE, Map.SPLIT, "sick pop flash", "then dog", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.VYSE, Map.BREEZE, "click heads", "just click the head", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.OMEN, Map.SUNSET, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.VIPER, Map.SPLIT, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.SAGE, Map.ICEBOX, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.RAZE, Map.BREEZE, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.ASTRA, Map.ICEBOX, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.KAYO, Map.ASCENT, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.NEON, Map.FRACTURE, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.FADE, Map.LOTUS, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.HARBOR, Map.FRACTURE, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.GEKKO, Map.BIND, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.DEADLOCK, Map.FRACTURE, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.ISO, Map.BIND, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.CLOVE, Map.BIND, "titleFour", "bodyFour", 3L, null, null));
            lineupRepository.createLineup(new Lineup(null, Agent.JETT, Map.FRACTURE, "titleFour", "bodyFour", 3L, null, null));

            likeRepository.likeLineup(new Like(1L, 2L, null));
            likeRepository.likeLineup(new Like(1L, 3L, null));
            likeRepository.likeLineup(new Like(1L, 11L, null));
            likeRepository.likeLineup(new Like(1L, 22L, null));
            likeRepository.likeLineup(new Like(1L, 18L, null));
            likeRepository.likeLineup(new Like(1L, 16L, null));
            likeRepository.likeLineup(new Like(2L, 2L, null));
            likeRepository.likeLineup(new Like(2L, 1L, null));
            likeRepository.likeLineup(new Like(2L, 22L, null));
            likeRepository.likeLineup(new Like(2L, 23L, null));
            likeRepository.likeLineup(new Like(2L, 12L, null));
            likeRepository.likeLineup(new Like(2L, 14L, null));
            likeRepository.likeLineup(new Like(3L, 4L, null));
            likeRepository.likeLineup(new Like(3L, 22L, null));
            likeRepository.likeLineup(new Like(3L, 15L, null));
            likeRepository.likeLineup(new Like(3L, 9L, null));
            likeRepository.likeLineup(new Like(3L, 20L, null));
            likeRepository.likeLineup(new Like(3L, 1L, null));

        };
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // Your frontend's origin
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
