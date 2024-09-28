package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;

// one way of forcing Spring to return ProblemDetail errors
// extends ResponseEntityExceptionHandler
// another one is to set "spring.mvc.problemdetails.enabled=true" in app.propertie/yaml


@RestControllerAdvice
public class GlobalExceptionController {

    private final String BAD_REQUEST = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/400";
    private final String NOT_FOUND = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404";

    // Consider redoing this at when opting for Spring's ProblemDetail thingies
    // Generic you gave me bad data pd
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid data");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        String error = String.format("Invalid value '%s' for parameter '%s'. Expected type: '%s'",
                e.getValue(), e.getName(), e.getRequiredType().getSimpleName());
        problemDetail.setDetail(error);
        return problemDetail;
    }

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

    // NB! this is different from the one above!
    // Invoked when fetching all lineups from a nonexistent user
    @ExceptionHandler(InvalidLineupException.NoUserException.class)
    public ProblemDetail handleNoUserExceptionException(InvalidLineupException.NoUserException e) {
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


    // feels bad to overwrite
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
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
        problemDetail.setType(URI.create(BAD_REQUEST));
        problemDetail.setProperty("time", Instant.now());

        return problemDetail;
    }

    // handle cases where we get enums that don't make sense
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        // assuming happy path for now...
        if (e.getCause() instanceof InvalidFormatException) {
            // determine if it's agent or map
            String invalidValue = extractInvalidValue(e.getMessage());
            if (e.getMessage().contains("SOVA")) {
                InvalidLineupException.InvalidAgentException ex = new InvalidLineupException.InvalidAgentException(invalidValue);
                return handleInvalidAgentException(ex);
            }
            if (e.getMessage().contains("ASCENT")) {
                InvalidLineupException.InvalidMapException ex = new InvalidLineupException.InvalidMapException(invalidValue);
                return handleInvalidMapException(ex);
            }
            problemDetail.setDetail("Invalid enum spotted!");
        }
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    // hacky solution but, should look for alternate solutions
    private static String extractInvalidValue(String message) {
        String prefix = "from String \"";
        String suffix = "\": not one of the values accepted";

        int startIndex = message.indexOf(prefix);
        if (startIndex != -1) {
            startIndex += prefix.length();
            int endIndex = message.indexOf(suffix, startIndex);
            if (endIndex != -1) {
                return message.substring(startIndex, endIndex);
            }
        }

        return null;  // Return null or throw an exception if not found
    }

    @ExceptionHandler(InvalidLineupException.InvalidAgentException.class)
    public ProblemDetail handleInvalidAgentException(InvalidLineupException.InvalidAgentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid agent");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.InvalidMapException.class)
    public ProblemDetail handleInvalidMapException(InvalidLineupException.InvalidMapException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid map");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid value");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }

    @ExceptionHandler(InvalidLineupException.BlankTitleException.class)
    public ProblemDetail handleBlankSearchTitleException(InvalidLineupException.BlankTitleException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid search title");
        problemDetail.setProperty("time", Instant.now());
        problemDetail.setType(URI.create(BAD_REQUEST));
        return problemDetail;
    }
}
