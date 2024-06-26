package dev.mordi.lineuplarry.lineup_larry_backend;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestClient;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.Lineup;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupRepository;
import dev.mordi.lineuplarry.lineup_larry_backend.user.User;
import dev.mordi.lineuplarry.lineup_larry_backend.user.UserRepository;

import java.util.Optional;

@SpringBootApplication
@EnableTransactionManagement
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(RestClient restClient, UserRepository userRepository,
			LineupRepository lineupRepository) {
		return args -> {
			User userOne = new User(null, "userOne");
			User userTwo = new User(null, "userOne");
			userRepository.createUser(userOne);
			userRepository.createUser(userTwo);

			Lineup lineupOne = new Lineup(null, "lineup one title", "lineup body", 1L); // same
			Lineup lineupTwo = new Lineup(2L, "lineup two title", "lineup body", 2L);
			Lineup lineupThree = new Lineup(3L, "lineup three title", "lineup body", 2L);
			lineupRepository.createLineup(lineupOne);
			lineupRepository.createLineup(lineupTwo);
			lineupRepository.createLineup(lineupThree);
		};
	}
}
