package dev.mordi.lineuplarry.lineup_larry_backend.like.exceptions;

public class InvalidLikeException extends RuntimeException {
  public InvalidLikeException(String message) {
    super(message);
  }

  public static class LikeNotFound extends InvalidLikeException {
    // TODO: reconsider if this is the best way to do this
    public LikeNotFound(Long userId, Long lineupId) {
      super(
          String.format(
              "The like between userId: '%d' and lineupId '%d' does not exist", userId, lineupId));
    }
  }
}
