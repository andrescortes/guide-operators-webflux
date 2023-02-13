package com.co.ias.moviesinfoservice.defaults;

import com.co.ias.moviesinfoservice.domain.movieinfo.MovieInfo;
import com.co.ias.moviesinfoservice.domain.movieinfo.gateway.MovieInfoGateway;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Configuration
public class RepositoryDefaultBeansConfig {

    private final MovieInfoGateway movieInfoGateway = new MovieInfoGateway() {
        @Override
        public Mono<MovieInfo> saveMovieInfo(MovieInfo movieInfo) {
            log.info("Using MovieInfoGateway.saveMovieInfo without to implement");
            return Mono.empty();
        }

        @Override
        public Mono<MovieInfo> updateMovieInfo(String movieInfoId, MovieInfo movieInfo) {
            log.info("Using MovieInfoGateway.updateMovieInfo without to implement");
            return Mono.empty();
        }

        @Override
        public Mono<MovieInfo> findMovieInfoById(String movieInfoId) {
            log.info("Using MovieInfoGateway.findMovieInfoById without to implement");
            return Mono.empty();
        }

        @Override
        public Mono<Void> deleteMovieInfo(MovieInfo movieInfo) {
            log.info("Using MovieInfoGateway.deleteMovieInfo without to implement");
            return Mono.empty();
        }

        @Override
        public Flux<MovieInfo> findAllMovieInfos() {
            log.info("Using MovieInfoGateway.findAllMovieInfos without to implement");
            return Flux.empty();
        }
    };


    private void alertFakeBean(String beanName) {
        log.log(Level.WARNING, "CONFIGURATION FAKE: " + beanName, beanName);
    }

    @Bean
    @ConditionalOnMissingBean
    public MovieInfoGateway movieInfoGateway() {
        alertFakeBean("MovieInfoGateway");
        return movieInfoGateway;
    }

}
