package com.reactivespring.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.reactivespring.domain.Movie;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

/**
 * The type Movies controller test.
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = {"restClient.moviesInfoURL = http://localhost:8084/v1/movieinfos",
    "restClient.reviewsURL = http://localhost:8084/v1/reviews"})
class MoviesControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
    }

    /**
     * Should retrieve movie by id.
     */
    @Test
    void shouldRetrieveMovieById() {
        // given
        String movieId = "abc";
        stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + movieId)).willReturn(
            aResponse().withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews")).willReturn(
            aResponse().withHeader("Content-Type", "application/json")
                .withBodyFile("reviews.json")));
        // when
        BodySpec<Movie, ?> movieBodySpec = webTestClient.get().uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus().isOk().expectBody(Movie.class);

        movieBodySpec.consumeWith(movieEntityExchangeResult -> {
            Movie responseBody = movieEntityExchangeResult.getResponseBody();
            assertNotNull(responseBody);
            assertEquals("1", responseBody.getMovieInfo().getMovieInfoId());
            assertEquals("Batman Begins", responseBody.getMovieInfo().getName());
            assertEquals(2, responseBody.getReviewList().size());
        });
    }

    /**
     * Should retrieve movie by id no found.
     */
    @Test
    void shouldRetrieveMovieByIdNoFound() {
        // given
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
            .willReturn(aResponse()
                .withStatus(404)
            ));

        stubFor(get(urlPathEqualTo("/v1/reviews")).willReturn(
            aResponse().withHeader("Content-Type", "application/json")
                .withBodyFile("reviews.json")));
        // when
        ResponseSpec xxClientError = webTestClient.get().uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus()
            .is4xxClientError();
//            .isNotFound();
        // then
        xxClientError.expectBody(String.class)
            .isEqualTo("There is no MovieInfo available for the passed in id: abc");
    }

    /**
     * Should retrieve movie by id reviews no found.
     */
    @Test
    void shouldRetrieveMovieByIdReviewsNoFound() {
        // given
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + movieId)).willReturn(
            aResponse().withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")
        ));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .willReturn(aResponse()
                .withStatus(500)
            ));

        // when
        ResponseSpec responseSpec = webTestClient.get().uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus().is5xxServerError();

        // then
        responseSpec.expectBody(Map.class).consumeWith(res->{
            Map responseBody = res.getResponseBody();
            assert responseBody != null;
            Assertions.assertEquals("/v1/movies/abc",responseBody.get("path"));
            Assertions.assertEquals(500,responseBody.get("status"));
            Assertions.assertEquals("Internal Server Error",responseBody.get("error"));
        });
    }
}

