package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiProblemException.class)
    public ProblemDetail handleApiProblemException(ApiProblemException e) {
        ProblemDetail problemDetail = e.getBody();
        withTimestamp(problemDetail);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(
        ConstraintViolationException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            e.getMessage()
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(
            URI.create(
                "https://lineup-larry.dev/problems/validation/constraint-violation"
            )
        );
        problemDetail.setProperty("code", "REQUEST_CONSTRAINT_VIOLATION");
        withTimestamp(problemDetail);
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException e,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        String parameterName =
            e instanceof MethodArgumentTypeMismatchException mismatch
                ? mismatch.getName()
                : "parameter";
        String expectedType =
            e.getRequiredType() != null
                ? e.getRequiredType().getSimpleName()
                : "unknown";
        String detail = String.format(
            "Invalid value '%s' for parameter '%s'. Expected type: '%s'",
            e.getValue(),
            parameterName,
            expectedType
        );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            detail
        );
        problemDetail.setTitle("Invalid data");
        problemDetail.setType(
            URI.create(
                "https://lineup-larry.dev/problems/request/invalid-parameter"
            )
        );
        problemDetail.setProperty("code", "REQUEST_INVALID_PARAMETER");
        withTimestamp(problemDetail);
        return handleExceptionInternal(
            e,
            problemDetail,
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            buildMethodArgumentDetail(e)
        );
        problemDetail.setTitle("Invalid data");
        problemDetail.setType(
            URI.create("https://lineup-larry.dev/problems/request/invalid-body")
        );
        problemDetail.setProperty("code", "REQUEST_INVALID_BODY");
        withTimestamp(problemDetail);
        return handleExceptionInternal(
            e,
            problemDetail,
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
        HandlerMethodValidationException e,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            buildHandlerValidationDetail(e)
        );
        problemDetail.setTitle("Invalid data");
        problemDetail.setType(
            URI.create("https://lineup-larry.dev/problems/request/invalid-body")
        );
        problemDetail.setProperty("code", "REQUEST_INVALID_BODY");
        withTimestamp(problemDetail);
        return handleExceptionInternal(
            e,
            problemDetail,
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException e,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        Throwable cause = e.getMostSpecificCause();
        if (isInvalidFormatException(cause)) {
            // TODO: consider not using Class<?>, hehe
            Class<?> targetType = getTargetType(cause);
            String invalidValue = getInvalidValue(cause);
            if (targetType == Agent.class) {
                return toProblemResponse(
                    new InvalidLineupException.InvalidAgentException(
                        invalidValue
                    ),
                    headers
                );
            }
            if (targetType == Map.class) {
                return toProblemResponse(
                    new InvalidLineupException.InvalidMapException(
                        invalidValue
                    ),
                    headers
                );
            }
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Invalid request content."
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(
            URI.create(
                "https://lineup-larry.dev/problems/request/unreadable-body"
            )
        );
        problemDetail.setProperty("code", "REQUEST_UNREADABLE_BODY");
        withTimestamp(problemDetail);
        return handleExceptionInternal(
            e,
            problemDetail,
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    private static String buildMethodArgumentDetail(
        MethodArgumentNotValidException e
    ) {
        List<String> messages = new ArrayList<>();
        e
            .getBindingResult()
            .getAllErrors()
            .forEach(error -> {
                String errorMessage = error.getDefaultMessage();
                if (error instanceof FieldError fieldError) {
                    messages.add(fieldError.getField() + ": " + errorMessage);
                    return;
                }
                messages.add(errorMessage);
            });
        return String.join("; ", messages);
    }

    private static String buildHandlerValidationDetail(
        HandlerMethodValidationException e
    ) {
        List<String> messages = new ArrayList<>();
        e
            .getParameterValidationResults()
            .forEach(result -> {
                if (result instanceof ParameterErrors parameterErrors) {
                    parameterErrors
                        .getFieldErrors()
                        .forEach(fieldError ->
                            messages.add(
                                fieldError.getField() +
                                    ": " +
                                    fieldError.getDefaultMessage()
                            )
                        );
                    return;
                }
                result
                    .getResolvableErrors()
                    .forEach(resolvableError -> {
                        if (resolvableError.getDefaultMessage() != null) {
                            messages.add(resolvableError.getDefaultMessage());
                        }
                    });
            });
        if (messages.isEmpty()) {
            return "Invalid request content.";
        }
        return String.join("; ", messages);
    }

    private static ResponseEntity<Object> toProblemResponse(
        ApiProblemException e,
        HttpHeaders headers
    ) {
        ProblemDetail problemDetail = e.getBody();
        withTimestamp(problemDetail);
        return new ResponseEntity<>(problemDetail, headers, e.getStatusCode());
    }

    private static boolean isInvalidFormatException(Throwable cause) {
        return (
            cause != null &&
            cause.getClass().getSimpleName().equals("InvalidFormatException")
        );
    }

    private static Class<?> getTargetType(Throwable cause) {
        try {
            Object result = cause
                .getClass()
                .getMethod("getTargetType")
                .invoke(cause);
            if (result instanceof Class<?> targetType) {
                return targetType;
            }
        } catch (ReflectiveOperationException ignored) {
            // fall through to null and generic unreadable-body response
        }
        return null;
    }

    private static String getInvalidValue(Throwable cause) {
        try {
            Object value = cause.getClass().getMethod("getValue").invoke(cause);
            return String.valueOf(value);
        } catch (ReflectiveOperationException ignored) {
            return "null";
        }
    }

    private static void withTimestamp(ProblemDetail problemDetail) {
        if (
            problemDetail.getProperties() == null ||
            !problemDetail.getProperties().containsKey("timestamp")
        ) {
            problemDetail.setProperty("timestamp", Instant.now());
        }
    }
}
