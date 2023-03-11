package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReviewsRestClient {

    private final WebClient webClient;
    @Value("${restClient.reviewsURL}")
    private String reviewUrl;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> reviewReviews(String movieId) {
        String uriString = UriComponentsBuilder.fromHttpUrl(reviewUrl)
            .queryParam("movieInfoId", movieId)
            .buildAndExpand().toUriString();
        System.out.println("uriString = " + uriString);
        return webClient.get()
            .uri(uriString)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response -> {
                if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    log.error("Error got status code: " + response.statusCode().value());
                    return Mono.error(new ReviewsClientException(
                        "There is no MovieInfo available for the passed in id: " + movieId,
                        response.statusCode().value()));
                }
                return response.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(
                        new ReviewsClientException(responseMessage,
                            response.statusCode().value())));
            })
            .onStatus(HttpStatus::is5xxServerError, response -> {
                if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                    log.error("Error got status code: " + response.statusCode().value());
                    return Mono.error(new ReviewsServerException(
                        "There is no Server available!",
                        response.statusCode().value()));
                }
                return response.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(
                        new ReviewsServerException(responseMessage,
                            response.statusCode().value())));
            })
            .bodyToFlux(Review.class)
/*            .onErrorMap(Predicate.not(ReviewsClientException.class::isInstance), throwable -> {
                log.error("Failed to send request to service", throwable);
                return new Exception("onErrorMap, Failed to send request to service", throwable);
            })*/
            .doOnError(
                error -> log.error("doOnError, Failed to send request to service at ends: {}",
                    error.getMessage()))
/*            .onErrorResume(WebClientResponseException.class, ex ->
                ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))*/
            .log("Item received: ", Level.INFO, true);
/*        return webClient.get()
            .uri(uriString)
            .retrieve()
            .bodyToFlux(Review.class)
            .log("Item received: ", Level.INFO, true);*/
    }
}
