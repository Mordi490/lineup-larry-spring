package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
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

            // add lineups
            lineupRepository.createLineup(new Lineup(null, Agent.SOVA, Map.ASCENT, "titleOne", "bodyOne", 1L));
            lineupRepository.createLineup(new Lineup(null, Agent.SOVA, Map.ASCENT, "titleTwo", "bodyTwo", 2L));
            lineupRepository.createLineup(new Lineup(null, Agent.BRIMSTONE, Map.BIND, "titleThree", "bodyThree", 2L));
            lineupRepository.createLineup(new Lineup(null, Agent.CYPHER, Map.SUNSET, "titleFour", "bodyFour", 3L));
            lineupRepository.createLineup(new Lineup(null, Agent.KILLJOY, Map.ICEBOX, "titleFour", "bodyFour", 3L));
            lineupRepository.createLineup(new Lineup(null, Agent.KILLJOY, Map.ICEBOX, "titleFour", "bodyFour", 3L));

            // more to come

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
