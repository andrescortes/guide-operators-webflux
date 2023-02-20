package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ReviewRouterIntegrationTest {

    private final String URL = "/v1/reviews";
    @Autowired
    private ReviewReactiveRepository repository;
    @Autowired
    private WebTestClient webTestClient;


    @BeforeEach
    void setUp() {
        List<Review> reviews = List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0)
        );
        repository.saveAll(reviews).blockLast();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void shouldAddReview() {
        // given
        Review awesomeMovie = new Review(null, 1L, "Awesome Movie", 9.0);
        // when
        webTestClient
            .post()
            .uri(URL)
            .body(Mono.just(awesomeMovie), Review.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Review.class)
            .consumeWith(res -> {
                Review responseBody = res.getResponseBody();
                Assertions.assertNotNull(responseBody);
                Assertions.assertEquals(awesomeMovie.getMovieInfoId(), responseBody.getMovieInfoId());
                Assertions.assertEquals(awesomeMovie.getComment(), responseBody.getComment());
                Assertions.assertEquals(awesomeMovie.getRating(), responseBody.getRating());
            });
    }
}
