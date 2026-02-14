package dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions;

import dev.mordi.lineuplarry.lineup_larry_backend.shared.ApiProblemException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.User;
import org.springframework.http.HttpStatus;

public abstract class InvalidUserException extends ApiProblemException {

    protected InvalidUserException(
        HttpStatus status,
        String problemSlug,
        String title,
        String detail,
        String code
    ) {
        super(status, problemSlug, title, detail, code);
    }

    public static class IdDoesNotMatchUserException
        extends InvalidUserException
    {

        public IdDoesNotMatchUserException(Long id, User user) {
            super(
                HttpStatus.BAD_REQUEST,
                "users/id-mismatch",
                "UserId is not valid",
                "Id does not match for this user\nExpected: '" +
                    id +
                    "' but got: '" +
                    user.id() +
                    "'",
                "USER_ID_MISMATCH"
            );
        }
    }

    public static class NullUsernameException extends InvalidUserException {

        public NullUsernameException() {
            super(
                HttpStatus.BAD_REQUEST,
                "users/username-null",
                "Username is null",
                "Username cannot be null",
                "USER_USERNAME_NULL"
            );
        }
    }

    public static class BlankUsernameException extends InvalidUserException {

        public BlankUsernameException() {
            super(
                HttpStatus.BAD_REQUEST,
                "users/username-blank",
                "Username is blank",
                "Username cannot be blank",
                "USER_USERNAME_BLANK"
            );
        }
    }

    public static class EmptyUsernameException extends InvalidUserException {

        public EmptyUsernameException() {
            super(
                HttpStatus.BAD_REQUEST,
                "users/username-empty",
                "Username is empty",
                "Username cannot be empty",
                "USER_USERNAME_EMPTY"
            );
        }
    }

    public static class UserNotFoundException extends InvalidUserException {

        public UserNotFoundException(Long id) {
            super(
                HttpStatus.NOT_FOUND,
                "users/not-found",
                "User not found",
                "User with id: '" + id + "' was not found",
                "USER_NOT_FOUND"
            );
        }
    }

    public static class IncludedUserIdException extends InvalidUserException {

        public IncludedUserIdException(Long id) {
            super(
                HttpStatus.BAD_REQUEST,
                "users/id-not-allowed",
                "The userId provided is not valid",
                "Do not supply id when creating a user\nThe id '" +
                    id +
                    "' is invalid",
                "USER_ID_NOT_ALLOWED"
            );
        }
    }
}
