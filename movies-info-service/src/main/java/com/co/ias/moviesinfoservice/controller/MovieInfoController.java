package com.co.ias.moviesinfoservice.controller;

import com.co.ias.moviesinfoservice.domain.movieinfo.MovieInfo;
import com.co.ias.moviesinfoservice.service.MoviesInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {

    @Autowired
    private MoviesInfoServiceImpl moviesInfoServiceImpl;

    @PostMapping("/movie-infos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return moviesInfoServiceImpl.saveMovieInfo(movieInfo);
    }
}
