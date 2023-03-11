package com.reactivespring.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionHandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@AutoConfigureWebTestClient
@ContextConfiguration(classes = {ReviewRouter.class,
    ReviewHandler.class, GlobalErrorHandler.class})// inject this beans to test
class ReviewRouterTest {

    private final String URL = "/v1/reviews";

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldAddReview() {

        // given
        Review awesomeMovie = new Review(null, 1L, "Awesome Movie", 9.0);
        // when
        Mockito.when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("123", 1L, "Awesome Movie", 9.0)));

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
    void shouldUpdateReview() {

        // given
        Review awesomeMovie = new Review("123", 1L, "Awesome Movie", 9.0);
        // when
        Mockito.when(reviewReactiveRepository.findById(anyString()))
            .thenReturn(Mono.just(awesomeMovie));
        Mockito.when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(awesomeMovie));

        ResponseSpec responseSpec = webTestClient
            .put()
            .uri(URL + "/{id}", awesomeMovie.getReviewId())
            .bodyValue(awesomeMovie)
//            .body(Mono.just(awesomeMovie), Review.class)
            .exchange()
            .expectStatus().isOk();
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
        Review awesomeMovie = new Review("123", 1L, "Awesome Movie", 9.0);
        // when
        Mockito.when(reviewReactiveRepository.findAll()).thenReturn(Flux.just(awesomeMovie));

        ResponseSpec responseSpec = webTestClient
            .get()
            .uri(URL)
            .exchange()
            .expectStatus().isOk();
        // then
        responseSpec
            .expectBodyList(Review.class)
            .hasSize(1)
            .consumeWith(listEntityExchangeResult -> {
                    List<Review> responseBody = listEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(awesomeMovie, responseBody.get(0));
                }
            );
    }

    @Test
    void shouldGetReviewByMovieReviewId() {

        // given
        Review awesomeMovie = new Review("123", 1L, "Awesome Movie", 9.0);
        // when
        Mockito.when(reviewReactiveRepository.findReviewsByMovieInfoId(anyLong()))
            .thenReturn(Flux.just(awesomeMovie));

        ResponseSpec responseSpec = webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(URL)
                .queryParam("movieInfoId", awesomeMovie.getMovieInfoId())
                .build()
            )
            .exchange()
            .expectStatus()
            .isOk();
        // then
        responseSpec
            .expectBodyList(Review.class)
            .hasSize(1)
            .consumeWith(listEntityExchangeResult -> {
                    List<Review> responseBody = listEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(awesomeMovie, responseBody.get(0));
                }
            );
    }

    @Test
    void shouldDeleteReviewByID() {
        // given
        String reviewId = "123";
        // when
        Mockito.when(reviewReactiveRepository.findById(reviewId))
            .thenReturn(Mono.just(new Review("123", 1L, "amazing", 9.5)));
        Mockito.when(reviewReactiveRepository.delete(isA(Review.class))).thenReturn(Mono.empty());

        ResponseSpec responseSpec = webTestClient
            .delete()
            .uri(URL + "/{id}", reviewId)
            .exchange()
            .expectStatus()
            .isNoContent();
        // then
        responseSpec
            .expectBody(Void.class);
    }

    @Test
    void updateReview() {
        //given

        Review reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);

        Mockito.when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));
        Mockito.when(reviewReactiveRepository.findById((String) any()))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        //when

        webTestClient
            .put()
            .uri("/v1/reviews/{id}", "abc")
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Review.class)
            .consumeWith(reviewResponse -> {
                var updatedReview = reviewResponse.getResponseBody();
                assert updatedReview != null;
                System.out.println("updatedReview : " + updatedReview);
                assertEquals(8.0, updatedReview.getRating());
                assertEquals("Not an Awesome Movie", updatedReview.getComment());
            });

    }

    @Test
    void shouldAddReviewValidation() {
        //given
        String errorMovieInfoId = "rating.movieInfoId: must not be null";
        String errorRating = "rating.negative : please pass a non-negative value";
        Review reviewUpdate = new Review(null, null, "Not an Awesome Movie", -8.4);

        Mockito.when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));

        //when
        ResponseSpec spec = webTestClient
            .post()
            .uri(URL)
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus()
            .isBadRequest();
        //then
        spec
            .expectBody(String.class)
            .consumeWith(res -> {
                String responseBody = res.getResponseBody();
                assertNotNull(responseBody);
                String[] errors = responseBody.split(",");
                assertEquals(2, errors.length);
                assertEquals(errorMovieInfoId, errors[0]);
                assertEquals(errorRating, errors[1]);
            });
    }

    @Test
    void updateReviewNotFound() {
        //given
        Review reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        Mockito.when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));
        Mockito.when(reviewReactiveRepository.findById("abc"))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        //when
        ResponseSpec spec = webTestClient
            .put()
            .uri("/v1/reviews/{id}", "abcd")
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus()
            .isNotFound();
        //then
        spec
           .expectBody(String.class)
           .consumeWith(res -> {
                String responseBody = res.getResponseBody();
                assertNotNull(responseBody);
                assertEquals("Not found!",responseBody);
            });

    }

}
