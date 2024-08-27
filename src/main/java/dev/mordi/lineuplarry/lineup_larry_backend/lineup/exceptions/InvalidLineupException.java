package dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions;

public class InvalidLineupException extends RuntimeException {
    public InvalidLineupException(String message) {
        super(message);
    }

    // A lot of these exist just to get validated in the service layer
    public static class EmptyTitleException extends InvalidLineupException {
        public EmptyTitleException() {
            super("Lineup title cannot be empty");
        }
    }

    public static class EmptyBodyException extends InvalidLineupException {
        public EmptyBodyException() {
            super("Lineup body cannot be empty");
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


    public static class UserIdNullException extends InvalidLineupException {
        public UserIdNullException() {
            super("UserId cannot be null");
        }
    }
    // the only ones we should need are the ones that aren't about null, blank or empty

    public static class EmptySearchTitleException extends InvalidLineupException {
        public EmptySearchTitleException(String str) {
            super("Cannot search for string: '" + str + "', since it's empty");
        }
    }

    public static class BlankSearchTitleException extends InvalidLineupException {
        public BlankSearchTitleException(String str) {
            super("Cannot search for string '" + str + "', since it's blank");
        }
    }

    public static class IncludedLineupIdException extends InvalidLineupException {
        public IncludedLineupIdException(Long id) {
            super("Do not supply an id when creating a lineup\nCannot create lineup with id: '" + id + "'");
        }
    }

    public static class UserIdInvalidException extends InvalidLineupException {
        public UserIdInvalidException(Long providedUserId) {
            super("You cannot create a lineup with userId: '" + providedUserId + "'");
        }
    }

    public static class NoUserException extends InvalidLineupException {
        public NoUserException(Long id) {
            super("No user with id: '" + id + "' exists");
        }
    }

    public static class ChangedLineupIdException extends InvalidLineupException {
        public ChangedLineupIdException(Long prevId, Long newId) {
            super("Cannot change lineup id from '" + prevId + "' to '" + newId + "'");
        }
    }
}
