package com.co.ias.moviesinfoservice.defaults;

import com.co.ias.moviesinfoservice.domain.movieinfo.MovieInfo;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class RepositoryDefaultBeansConfigTest {

    private final RepositoryDefaultBeansConfig config = new RepositoryDefaultBeansConfig();

    @Test
    void saveMovieInfo() {
        Mono<MovieInfo> movieInfoMono = config.movieInfoGateway().saveMovieInfo(null);

        StepVerifier.create(movieInfoMono)
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        Mono<MovieInfo> movieInfoMono = config.movieInfoGateway().updateMovieInfo(null,null);

        StepVerifier.create(movieInfoMono)
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void findMovieInfoById() {
        Mono<MovieInfo> movieInfoMono = config.movieInfoGateway().findMovieInfoById(null);

        StepVerifier.create(movieInfoMono)
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        Mono<Void> movieInfoMono = config.movieInfoGateway().deleteMovieInfo(null);

        StepVerifier.create(movieInfoMono)
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void findAllMovieInfos() {
        Flux<MovieInfo> movieInfoFlux = config.movieInfoGateway().findAllMovieInfos();

        StepVerifier.create(movieInfoFlux)
            .expectNextCount(0)
            .verifyComplete();
    }
}
