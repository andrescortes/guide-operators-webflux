package com.co.ias.moviesinfoservice.controller;

import com.co.ias.moviesinfoservice.controller.dto.MovieInfoDTO;
import com.co.ias.moviesinfoservice.controller.transformers.MovieInfoTransformer;
import com.co.ias.moviesinfoservice.domain.MovieInfo;
import com.co.ias.moviesinfoservice.service.MoviesInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {
    private final MovieInfoTransformer movieInfoTransformer;

    private final MoviesInfoService moviesInfoService;
    Sinks.Many<MovieInfo> movieInfoMany = Sinks.many().replay().all();

    public MovieInfoController(
        MovieInfoTransformer movieInfoTransformer, MoviesInfoService moviesInfoService) {
        this.movieInfoTransformer = movieInfoTransformer;

        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(
        @RequestParam(value = "year", required = false) Integer year) {
        if (year != null) {
            return moviesInfoService.getMovieInfosByYear(year);
        }
        return moviesInfoService.getAllMovieInfos();
    }

    @GetMapping("/movieinfos/{movieInfoId}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String movieInfoId) {
        return moviesInfoService.getMovieInfoById(movieInfoId)
            .map(ResponseEntity.ok()::body)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/movieinfos/{movieInfoId}")
    public Mono<Void> deleteMovieInfo(@PathVariable String movieInfoId) {
        return moviesInfoService.deleteMovieInfo(movieInfoId)
            .switchIfEmpty(
                Mono.error(
                    new IllegalArgumentException("No found entity with id: " + movieInfoId)));
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfoDTO movieInfoDTO) {
        return moviesInfoService.addMovieInfo(movieInfoTransformer.toEntity(movieInfoDTO))
            .doOnNext(movieInfo -> movieInfoMany.tryEmitNext(movieInfo));
    }

    @GetMapping(value = "/movieinfos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> streamMovieInfoSink() {
        return movieInfoMany.asFlux();
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id,
        @RequestBody MovieInfoDTO movieInfoDTO) {
        return moviesInfoService.updateMovieInfo(id, movieInfoTransformer.toEntity(movieInfoDTO))
            .map(ResponseEntity.ok()::body)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
