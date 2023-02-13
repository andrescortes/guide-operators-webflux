package com.co.ias.moviesinfoservice.domain.movieinfo.gateway;

import com.co.ias.moviesinfoservice.domain.movieinfo.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoGateway {

    Mono<MovieInfo> saveMovieInfo(MovieInfo movieInfo);

    Mono<MovieInfo> updateMovieInfo(String movieInfoId, MovieInfo movieInfo);

    Mono<MovieInfo> findMovieInfoById(String movieInfoId);

    Mono<Void> deleteMovieInfo(MovieInfo movieInfo);

    Flux<MovieInfo> findAllMovieInfos();
}
