package com.reactivespring.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
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
        ResponseSpec responseSpec = webTestClient
            .post()
            .uri(URL)
            .bodyValue(awesomeMovie)
//            .body(Mono.just(awesomeMovie), Review.class)
            .exchange()
            .expectStatus().isCreated();
        // then
        responseSpec
            .expectBody(Review.class)
            .consumeWith(res -> {
                Review responseBody = res.getResponseBody();
                assertNotNull(responseBody);
                assertEquals(awesomeMovie.getMovieInfoId(),
                    responseBody.getMovieInfoId());
                assertEquals(awesomeMovie.getComment(), responseBody.getComment());
                assertEquals(awesomeMovie.getRating(), responseBody.getRating());
            });
    }

    @Test
    void shouldGetReviews() {
        // given
        // when
        ResponseSpec responseSpec = webTestClient
            .get()
            .uri(URL)
            .exchange()
            .expectStatus().isOk();
        // then
        responseSpec.expectBodyList(Review.class)
            .consumeWith(res -> {
                List<Review> responseBody = res.getResponseBody();
                HttpStatus status = res.getStatus();
                assertNotNull(responseBody);
                Assertions.assertTrue(status.is2xxSuccessful());
                assertEquals(3, responseBody.size());
            });
    }

    @Test
    void shouldUpdateReviews() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = repository.save(review).block();
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        //when
        assert savedReview != null;

        webTestClient
            .put()
            .uri(URL+"/{id}", savedReview.getReviewId())
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Review.class)
            .consumeWith(reviewResponse -> {
                var updatedReview = reviewResponse.getResponseBody();
                assert updatedReview != null;
                System.out.println("updatedReview : " + updatedReview);
                assertNotNull(savedReview.getReviewId());
                assertEquals(8.0, updatedReview.getRating());
                assertEquals("Not an Awesome Movie", updatedReview.getComment());
            });
    }

    @Test
    void shouldDeleteReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = repository.save(review).block();
        //when
        assert savedReview != null;
        webTestClient
            .delete()
            .uri(URL+"/{id}", savedReview.getReviewId())
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(URL)
                .queryParam("movieInfoId", "1")
                .build())
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .value(reviewList -> {
                System.out.println("reviewList : " + reviewList);
                assertEquals(2, reviewList.size());
            });

    }
}
