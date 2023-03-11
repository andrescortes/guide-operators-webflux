package com.reactivespring.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.*;

import com.reactivespring.domain.Movie;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(
    port = 8084
)
@TestPropertySource(
    properties = {
    "restClient.moviesInfoURL = http://localhost:8084/v1/movieinfos",
    "restClient.reviewsURL = http://localhost:8084/v1/reviews"
})
class MoviesControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldRetrieveMovieById() {
        // given
        String movieId = "abc";
        stubFor(get(urlEqualTo("/v1/movieinfos" + "/"+movieId))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")
            )
        );
        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("reviews.json")
            )
        );
        // when
        BodySpec<Movie, ?> movieBodySpec = webTestClient.get()
            .uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Movie.class);

        movieBodySpec.consumeWith(movieEntityExchangeResult -> {
            Movie responseBody = movieEntityExchangeResult.getResponseBody();
            assertNotNull(responseBody);
            assertEquals("1",responseBody.getMovieInfo().getMovieInfoId());
            assertEquals("Batman Begins",responseBody.getMovieInfo().getName());
            assertEquals(2,responseBody.getReviewList().size());
            byte[] requestBodyContent = movieEntityExchangeResult.getRequestBodyContent();
            URI url = movieEntityExchangeResult.getUrl();
            int rawStatusCode = movieEntityExchangeResult.getRawStatusCode();

        });
    }
}
