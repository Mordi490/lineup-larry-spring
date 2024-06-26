package dev.mordi.lineuplarry.lineup_larry_backend.lineup;


public record Lineup(Long id, String title, String body, Long userId) {
    // helper factory method
    public static Lineup create(Long id, String title, String body, Long userId) {
        return new Lineup(id, title, body, userId);
    }
}
