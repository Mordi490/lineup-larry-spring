package dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions;

import dev.mordi.lineuplarry.lineup_larry_backend.user.User;

public class IdDoesNotMatchUserException extends RuntimeException {
    public IdDoesNotMatchUserException(Long id, User user) {
        super("Id " + id + " does not match user " + user.username() + "current id");
    }
}
