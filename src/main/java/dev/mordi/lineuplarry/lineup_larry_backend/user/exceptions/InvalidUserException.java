package dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions;

import dev.mordi.lineuplarry.lineup_larry_backend.user.User;

public class InvalidUserException extends RuntimeException {

  public InvalidUserException(String message) {
    super(message);
  }

  public static class IdDoesNotMatchUserException extends InvalidUserException {
    public IdDoesNotMatchUserException(Long id, User user) {
      super("Id does not match for this user\nExpected: '" + id + "' but got: '" + user.id() + "'");
    }
  }

  public static class NullUsernameException extends InvalidUserException {
    public NullUsernameException() {
      super("Username cannot be null");
    }
  }

  public static class BlankUsernameException extends InvalidUserException {
    public BlankUsernameException() {
      super("Username cannot be blank");
    }
  }

  public static class EmptyUsernameException extends InvalidUserException {
    public EmptyUsernameException() {
      super("Username cannot be empty");
    }
  }

  public static class UserNotFoundException extends InvalidUserException {
    public UserNotFoundException(Long id) {
      super("User with id: '" + id + "' was not found");
    }
  }

  public static class IncludedUserIdException extends InvalidUserException {
    public IncludedUserIdException(Long id) {
      super("Do not supply id when creating a user\nThe id '" + id + "' is invalid");
    }
  }
}
