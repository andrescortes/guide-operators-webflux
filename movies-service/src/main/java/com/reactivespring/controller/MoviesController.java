package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.Review;
import java.util.List;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/v1/movies")
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewsRestClient reviewsRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient,
        ReviewsRestClient reviewsRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{movieId}")
    public Mono<Movie> retrieveMovieById(@PathVariable String movieId) {
        System.out.println("movieId = " + movieId);
        return moviesInfoRestClient.retrieveMovieInfo(movieId).log("MovieInfoRestClient: ", Level.INFO, true)
            .flatMap(movieInfo -> {
                System.out.println("movieInfo = " + movieInfo.getCast());
                Mono<List<Review>> listMono = reviewsRestClient.reviewReviews(movieId)
                    .collectList();
                return listMono.map(reviews -> new Movie(movieInfo, reviews));
            });
    }
}
