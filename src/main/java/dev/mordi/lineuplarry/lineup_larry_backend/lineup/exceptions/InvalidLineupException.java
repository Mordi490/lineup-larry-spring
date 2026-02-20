package dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions;

import org.springframework.http.HttpStatus;

import dev.mordi.lineuplarry.lineup_larry_backend.shared.ApiProblemException;

public abstract class InvalidLineupException extends ApiProblemException {

    protected InvalidLineupException(HttpStatus status, String problemSlug, String title,
            String detail, String code) {
        super(status, problemSlug, title, detail, code);
    }

    // A lot of these exist just to get validated in the service layer
    public static class EmptyTitleException extends InvalidLineupException {

        public EmptyTitleException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/title-empty",
                    "Lineup title is empty",
                    "Lineup title cannot be empty",
                    "LINEUP_TITLE_EMPTY");
        }
    }

    public static class EmptyBodyException extends InvalidLineupException {

        public EmptyBodyException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/body-empty",
                    "Lineup body is empty",
                    "Lineup body cannot be empty",
                    "LINEUP_BODY_EMPTY");
        }
    }

    public static class BlankTitleException extends InvalidLineupException {

        public BlankTitleException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/title-blank",
                    "Invalid search title",
                    "Lineup title cannot be blank",
                    "LINEUP_TITLE_BLANK");
        }
    }

    public static class BlankBodyException extends InvalidLineupException {

        public BlankBodyException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/body-blank",
                    "Lineup body is blank",
                    "Lineup body cannot be blank",
                    "LINEUP_BODY_BLANK");
        }
    }

    public static class NullTitleException extends InvalidLineupException {

        public NullTitleException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/title-null",
                    "Lineup title is null",
                    "Lineup title cannot be null",
                    "LINEUP_TITLE_NULL");
        }
    }

    public static class NullBodyException extends InvalidLineupException {

        public NullBodyException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/body-null",
                    "Lineup body is null",
                    "Lineup body cannot be null",
                    "LINEUP_BODY_NULL");
        }
    }

    public static class UserIdNullException extends InvalidLineupException {

        public UserIdNullException() {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/user-id-null",
                    "Lineup user id is null",
                    "UserId cannot be null",
                    "LINEUP_USER_ID_NULL");
        }
    }

    public static class EmptySearchTitleException extends InvalidLineupException {

        public EmptySearchTitleException(String str) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/search-title-empty", "Search title is empty",
                    "Cannot search for string: '" + str + "', since it's empty",
                    "LINEUP_SEARCH_TITLE_EMPTY");
        }
    }

    public static class BlankSearchTitleException extends InvalidLineupException {

        public BlankSearchTitleException(String str) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/search-title-blank",
                    "Search title is blank",
                    "Cannot search for string: '" + str + "', since it's blank",
                    "LINEUP_SEARCH_TITLE_BLANK");
        }
    }

    public static class IncludedLineupIdException extends InvalidLineupException {

        public IncludedLineupIdException(Long id) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/id-not-allowed",
                    "Invalid lineup id",
                    "Do not supply an id when creating a lineup\nCannot create lineup with id: '"
                            + id + "'",
                    "LINEUP_ID_NOT_ALLOWED");
        }
    }

    public static class InvalidAgentException extends InvalidLineupException {

        public InvalidAgentException(String str) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/invalid-agent",
                    "Invalid agent",
                    "The agent: '" + str + "' is not a valid agent",
                    "LINEUP_INVALID_AGENT");
        }
    }

    public static class InvalidMapException extends InvalidLineupException {

        public InvalidMapException(String str) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/invalid-map",
                    "Invalid map",
                    "The map: '" + str + "' is not a valid map",
                    "LINEUP_INVALID_MAP");
        }
    }

    public static class UserIdInvalidException extends InvalidLineupException {

        public UserIdInvalidException(Long providedUserId) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/user-id-invalid",
                    "Invalid user id",
                    "You cannot create a lineup with userId: '" + providedUserId + "'",
                    "LINEUP_USER_ID_INVALID");
        }
    }

    public static class NoUserException extends InvalidLineupException {

        public NoUserException(Long id) {
            super(HttpStatus.NOT_FOUND,
                    "lineups/user-not-found",
                    "User not found",
                    "No user with id: '" + id + "' exists",
                    "LINEUP_USER_NOT_FOUND");
        }
    }

    public static class ChangedLineupIdException extends InvalidLineupException {

        public ChangedLineupIdException(Long prevId, Long newId) {
            super(HttpStatus.BAD_REQUEST,
                    "lineups/id-mismatch",
                    "Lineup id's cannot be altered",
                    "Cannot change lineup id from: '" + prevId + "' to: '" + newId + "'",
                    "LINEUP_ID_MISMATCH");
        }
    }

    public static class NoSuchLineupException extends InvalidLineupException {

        public NoSuchLineupException(Long lineupId) {
            super(HttpStatus.NOT_FOUND,
                    "lineups/not-found",
                    "Lineup not found",
                    "No lineup with id: '" + lineupId + "' exists",
                    "LINEUP_NOT_FOUND");
        }
    }
}
