package dev.mordi.lineuplarry.lineup_larry_backend.lineup;


public record Lineup(Long id, String title, String body, Long userId) {
    // helper factory method
    public static Lineup create(Long id, String title, String body, Long userId) {
        return new Lineup(id, title, body, userId);
    }

    // Method to recreate everything but the title
    public Lineup withTitle(String newTitle) {
        return new Lineup(this.id, newTitle, this.body, this.userId);
    }

    // Method to recreate everything but the body
    public Lineup withBody(String newBody) {
        return new Lineup(this.id, this.title, newBody, this.userId);
    }

    // Method to recreate everything but the userId
    public Lineup withUserId(Long newUserId) {
        return new Lineup(this.id, this.title, this.body, newUserId);
    }

    // Method to recreate everything but the id
    public Lineup withId(Long newId) {
        return new Lineup(newId, this.title, this.body, this.userId);
    }

}
