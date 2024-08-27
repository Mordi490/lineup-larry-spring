package dev.mordi.lineuplarry.lineup_larry_backend;


import org.springframework.boot.SpringApplication;

public class TestLineupLarryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }
}
