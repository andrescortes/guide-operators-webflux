package com.co.ias.moviesinfoservice.service;

import com.co.ias.moviesinfoservice.domain.MovieInfo;
import com.co.ias.moviesinfoservice.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {


    private final MovieInfoRepository movieInfoRepository;


    public MoviesInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {

        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String movieInfoId) {
        return movieInfoRepository.findById(movieInfoId);
    }

    public Mono<MovieInfo> updateMovieInfo(String movieInfoId, MovieInfo movieInfo) {
        return movieInfoRepository.findById(movieInfoId)
            .flatMap(s -> {
                s.setYear(movieInfo.getYear());
                s.setCast(movieInfo.getCast());
                s.setName(movieInfo.getName());
                s.setReleaseDate(movieInfo.getReleaseDate());
                return movieInfoRepository.save(s);
            });
    }

    public Mono<Void> deleteMovieInfo(String movieInfoId) {
        return movieInfoRepository.deleteById(movieInfoId);
    }

    public Flux<MovieInfo> getMovieInfosByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
}
