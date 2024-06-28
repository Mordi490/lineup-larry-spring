package dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(String message) {
        super(message);
    }

    public static class NullUsernameException extends InvalidUsernameException {
        public NullUsernameException() {
            super("Username cannot be null");
        }
    }

    public static class EmptyUsernameException extends InvalidUsernameException {
        public EmptyUsernameException() {
            super("Username cannot be empty");
        }
    }

    public static class BlankUsernameException extends InvalidUsernameException {
        public BlankUsernameException() {
            super("Username cannot be blank");
        }
    }
}
