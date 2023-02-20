package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private final ReviewReactiveRepository repository;

    public ReviewHandler(ReviewReactiveRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        // to perform operations use flatmap
        return request.bodyToMono(Review.class) // extract from request to cast it at Review.class
            .flatMap(repository::save)// mono of Review
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);// mono of ServerResponse
    }
}
