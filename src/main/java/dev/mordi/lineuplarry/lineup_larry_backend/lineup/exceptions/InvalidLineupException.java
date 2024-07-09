package dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions;

public class InvalidLineupException extends RuntimeException {
    public InvalidLineupException(String message) {
        super(message);
    }

    public static class EmptyTitleException extends InvalidLineupException {
        public EmptyTitleException() {
            super("Lineup title cannot be empty");
        }
    }

    public static class EmptyBodyException extends InvalidLineupException {
        public EmptyBodyException() {
            super("Lineup title cannot be empty");
        }
    }

    public static class EmptyLineupIdException extends InvalidLineupException {
        public EmptyLineupIdException() {
            super("Lineup id cannot be empty");
        }
    }

    public static class BlankTitleException extends InvalidLineupException {
        public BlankTitleException() {
            super("Lineup title cannot be blank");
        }
    }

    public static class BlankBodyException extends InvalidLineupException {
        public BlankBodyException() {
            super("Lineup body cannot be blank");
        }
    }

    public static class NullTitleException extends InvalidLineupException {
        public NullTitleException() {
            super("Lineup title cannot be null");
        }
    }

    public static class NullBodyException extends InvalidLineupException {
        public NullBodyException() {
            super("Lineup body cannot be null");
        }
    }

    public static class NullLineupIdException extends InvalidLineupException {
        public NullLineupIdException() {
            super("Lineup id cannot be null");
        }
    }

    public static class UserIdInvalidException extends InvalidLineupException {
        public UserIdInvalidException() {
            super("The provided userId does not belong to you");
        }
    }

    public static class UserIdNullException extends InvalidLineupException {
        public UserIdNullException() {
            super("UserId cannot be null");
        }
    }

    public static class UserIdDoesNotHaveUserException extends InvalidLineupException {
        public UserIdDoesNotHaveUserException() {
            super("The provided UserId does not match with a current user in the db");
        }
    }
}
