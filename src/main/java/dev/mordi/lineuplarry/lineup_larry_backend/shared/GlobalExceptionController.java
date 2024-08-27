package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Errors follow the ProblemDetails RFC
 * <p>
 * Structure explanations:
 * {
 * type: about:blank // TODO: find a good way to use this field
 * title: No user found // a generic, short, concise explanation
 * status: 404
 * detail: No user exists for the provided id // A more specific explanation, ie. includes the data we deem as faulty.
 * instance: /api/lineups/user/999
 * time: 2024-07-29T19:44:03.127548222Z
 * <p>
 * }
 * <p>
 * Example:
 * {
 * type: someshit
 * title: No user found
 * status: 404
 * detail: No user found with id 999
 * instance: /api/lineups/user/999
 * time: 2024-07-29T19:44:03.127548222Z
 * }
 */
@RestControllerAdvice
public class GlobalExceptionController {

    private final String BAD_REQUEST = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/400";
    private final String NOT_FOUND = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404";


    // User Exceptions
    @ExceptionHandler(InvalidUserException.IdDoesNotMatchUserException.class)
    public ProblemDetail handleIdDoesNotMatchUser(InvalidUserException.IdDoesNotMatchUserException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("UserId is not valid");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidUserException.BlankUsernameException.class)
    public ProblemDetail handleBlankUsername(InvalidUserException.BlankUsernameException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Username is blank");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidUserException.EmptyUsernameException.class)
    public ProblemDetail handleEmptyUsername(InvalidUserException.EmptyUsernameException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Username is empty");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidUserException.NullUsernameException.class)
    public ProblemDetail handleNullUsername(InvalidUserException.NullUsernameException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Username is null");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidUserException.MissingCreateDataException.class)
    public ProblemDetail MissingCreateDataException(InvalidUserException.MissingCreateDataException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid data to create user");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.ChangedLineupIdException.class)
    public ProblemDetail handleChangedLineupIdException(InvalidLineupException.ChangedLineupIdException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Lineup id's cannot be altered");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidUserException.UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(InvalidUserException.UserNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("User not found");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(NOT_FOUND));
        return problemDetail;
    }

    @ExceptionHandler(InvalidUserException.IncludedUserIdException.class)
    public ProblemDetail handleIncludedUserIdException(InvalidUserException.IncludedUserIdException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("The userId provided is not valid");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.NoUserException.class)
    public ProblemDetail handleNoSuchUser(InvalidLineupException.NoUserException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("User not found");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(NOT_FOUND));
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.EmptySearchTitleException.class)
    public ProblemDetail handleEmptySearchTitleException(InvalidLineupException.EmptySearchTitleException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Search title cannot be empty");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.BlankSearchTitleException.class)
    public ProblemDetail handleBlankSearchTitleException(InvalidLineupException.BlankSearchTitleException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Search title cannot be blank");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    /*
    @ExceptionHandler(InvalidLineupException.NullBodyException.class)
    public ProblemDetail handleNullBodyException(InvalidLineupException.NullBodyException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.UserIdNullException.class)
    @ExceptionHandler(InvalidLineupException.EmptyTitleException.class)
    @ExceptionHandler(InvalidLineupException.EmptyBodyException.class)
    @ExceptionHandler(InvalidLineupException.BlankTitleException.class)
    @ExceptionHandler(InvalidLineupException.BlankBodyException.class)
     */

    // TODO: consider having this just return the most pressing concern for each FieldError
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException e) {
        // Collect validation errors
        StringBuilder detailBuilder = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            if (!detailBuilder.isEmpty()) {
                detailBuilder.append("; ");
            }
            // putting this aside as it looks ugly, but keeping it since it might make
            // it easier to read when multiples are wrong
            detailBuilder.append(fieldName).append(": ").append(errorMessage);
        });

        // Create ProblemDetails object
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailBuilder.toString());
        problemDetail.setTitle("Invalid data");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));

        return problemDetail;
    }
}
