package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewHandler {

    private final ReviewReactiveRepository repository;

    @Autowired
    private Validator validator;

    public ReviewHandler(ReviewReactiveRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        // to perform operations use flatmap
        return request.bodyToMono(Review.class)// extract from request to cast it at Review.class
            .doOnNext(this::validate)
            .flatMap(repository::save)// mono of Review
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);// mono of ServerResponse
    }

    private void validate(Review review) {
        Set<ConstraintViolation<Review>> constraintViolations = validator.validate(review);
        log.info("Errors: {}", constraintViolations);
        if (constraintViolations.size() > 0) {
            String errors = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(","));
            throw new ReviewDataException(errors);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        Optional<String> movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            Flux<Review> reviewsByMovieInfoId = repository.findReviewsByMovieInfoId(
                Long.valueOf(movieInfoId.get()));
            return ServerResponse.ok().body(reviewsByMovieInfoId, Review.class);
        } else {
            Flux<Review> reviewFlux = repository.findAll();
            return ServerResponse.ok().body(reviewFlux, Review.class);
        }
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Review> reviewMono = request.bodyToMono(Review.class);
        return reviewMono
            .flatMap(review ->
                repository.findById(id)
                    .switchIfEmpty(Mono.error(new ReviewNotFoundException("No found!")))
                    .map(reviewRecovered -> {
                        reviewRecovered.setMovieInfoId(review.getMovieInfoId());
                        reviewRecovered.setComment(review.getComment());
                        reviewRecovered.setRating(review.getRating());
                        return repository.save(reviewRecovered);
                    })
                    .flatMap(ServerResponse.status(HttpStatus.OK)::bodyValue));
    }

    public Mono<ServerResponse> updateReview2(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Review> existingReview = repository.findById(id).switchIfEmpty(Mono.error(new ReviewNotFoundException("No found!")));
        if (existingReview.block() == null){
            return ServerResponse.status(HttpStatus.NOT_FOUND).build();
        }
        return existingReview
            .flatMap(review -> request.bodyToMono(Review.class)
                .map(requestReview -> {
                    review.setRating(requestReview.getRating());
                    review.setComment(requestReview.getComment());
                    return review;
                })
                .flatMap(repository::save)
                .flatMap(
                    requestSaved -> ServerResponse.status(HttpStatus.OK).bodyValue(requestSaved))
            );
            //.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        return repository.findById(request.pathVariable("id"))
            .flatMap(repository::delete)
            .then(ServerResponse.noContent().build());
    }
}
