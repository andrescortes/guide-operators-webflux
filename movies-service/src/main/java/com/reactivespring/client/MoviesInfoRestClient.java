package com.reactivespring.client;

import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import java.time.Duration;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Slf4j
@Component
public class MoviesInfoRestClient {

    private final WebClient webClient;
    @Value("${restClient.moviesInfoURL}")
    private String moviesInfoUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        String url = moviesInfoUrl.concat("/{id}");
        return webClient.
            get()
            .uri(url, movieId)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response -> {
                if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    log.error("Error got status code: " + response.statusCode().value());
                    return Mono.error(new MoviesInfoClientException(
                        "There is no MovieInfo available for the passed in id: " + movieId,
                        response.statusCode().value()));
                }
                return response.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(
                        new MoviesInfoClientException(responseMessage,
                            response.statusCode().value())));
            })
            .onStatus(HttpStatus::is5xxServerError, response -> {
                if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                    log.error("Error got status code: " + response.statusCode().value());
                    return Mono.error(new MoviesInfoServerException(
                        "There is no Server available!",
                        response.statusCode().value()));
                }
                return response.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(
                        new MoviesInfoServerException(responseMessage,
                            response.statusCode().value())));
            })
            .bodyToMono(MovieInfo.class)
//            .retry(3)
            .retryWhen(RetryUtil.retrySpec())
            .doOnError(error -> log.error("Failed to send request to service at ends: {}",
                error.getMessage(), error))
            .log("Item received: ", Level.INFO, true);

    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        String url = moviesInfoUrl.concat("/stream");
       return webClient.get().uri(url)
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError, response -> {
                if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                    log.error("Error got status code: " + response.statusCode().value());
                    return Mono.error(new MoviesInfoServerException(
                        "There is no Server available!",
                        response.statusCode().value()));
                }
                return response.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(
                        new MoviesInfoServerException(responseMessage,
                            response.statusCode().value())));
            })
            .bodyToFlux(MovieInfo.class)
//            .retry(3)
            .retryWhen(RetryUtil.retrySpec())
            .doOnError(error -> log.error("Failed to send request to service at ends: {}",
                error.getMessage(), error))
            .log("Item received: ", Level.INFO, true);

    }
}
