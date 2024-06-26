package dev.mordi.lineuplarry.lineup_larry_backend.user;


public record User(Long id, String username) {
    // helper factory method
    public static User create(Long id, String username) {
        return new User(id, username);
    }
}
