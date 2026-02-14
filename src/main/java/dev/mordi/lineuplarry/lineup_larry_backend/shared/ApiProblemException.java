package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public abstract class ApiProblemException extends ErrorResponseException {

    // Base exception for domain errors that should be returned as Problem Details with a stable type/code.
    private static final String PROBLEM_BASE_URI =
        "https://lineup-larry.dev/problems/";

    protected ApiProblemException(
        HttpStatus status,
        String problemSlug,
        String title,
        String detail,
        String code
    ) {
        super(
            status,
            createProblemDetail(status, problemSlug, title, detail, code),
            null
        );
    }

    private static ProblemDetail createProblemDetail(
        HttpStatus status,
        String problemSlug,
        String title,
        String detail,
        String code
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            status,
            detail
        );
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + problemSlug));
        problemDetail.setProperty("code", code);
        return problemDetail;
    }

    @Override
    public String getMessage() {
        ProblemDetail problemDetail = getBody();
        if (problemDetail != null && problemDetail.getDetail() != null) {
            return problemDetail.getDetail();
        }
        return super.getMessage();
    }
}
