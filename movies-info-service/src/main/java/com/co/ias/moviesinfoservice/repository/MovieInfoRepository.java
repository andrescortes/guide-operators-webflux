package com.co.ias.moviesinfoservice.repository;

import com.co.ias.moviesinfoservice.domain.movieinfo.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

}
