package dev.mordi.lineuplarry.lineup_larry_backend.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

public abstract class RestIntegrationTestSupport {

    @Autowired
    protected RestTestClient client;

    protected <T> T getOkBody(String uri, ParameterizedTypeReference<T> responseType) {
        return client.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(responseType)
                .returnResult()
                .getResponseBody();
    }

    protected String getBody(String uri, HttpStatus expectedStatus) {
        return client.get()
                .uri(uri)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
    }
}
