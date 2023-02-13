package com.co.ias.moviesinfoservice.service;

import com.co.ias.moviesinfoservice.domain.common.constants.Constants;
import com.co.ias.moviesinfoservice.domain.movieinfo.MovieInfo;
import com.co.ias.moviesinfoservice.domain.movieinfo.gateway.MovieInfoGateway;
import com.co.ias.moviesinfoservice.domain.common.exceptions.MovieInfoNoFoundException;
import com.co.ias.moviesinfoservice.repository.MovieInfoRepository;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoServiceImpl implements MovieInfoGateway {

    @Autowired
    private MovieInfoRepository repository;

    @Override
    public Mono<MovieInfo> saveMovieInfo(MovieInfo movieInfo) {
        return repository.save(movieInfo);
    }

    @Override
    public Mono<MovieInfo> updateMovieInfo(String movieInfoId, MovieInfo movieInfo) {
        return repository.findById(movieInfoId)
            .flatMap(s -> repository.save(movieInfo))
            .switchIfEmpty(
                Mono.error(new MovieInfoNoFoundException(Constants.NO_FOUND_MOVIE_INFO)));
    }

    @Override
    public Mono<MovieInfo> findMovieInfoById(String movieInfoId) {
        return repository.findById(movieInfoId)
            .switchIfEmpty(
                Mono.error(new MovieInfoNoFoundException(Constants.NO_FOUND_MOVIE_INFO)));
    }


    @Override
    public Mono<Void> deleteMovieInfo(MovieInfo movieInfo) {
        return repository.delete(movieInfo);
    }

    @Override
    public Flux<MovieInfo> findAllMovieInfos() {
        return repository.findAll()
            .switchIfEmpty(Flux.just(new MovieInfo("test", "test", 3000,
                Collections.emptyList(), LocalDate.now())));
    }
}
