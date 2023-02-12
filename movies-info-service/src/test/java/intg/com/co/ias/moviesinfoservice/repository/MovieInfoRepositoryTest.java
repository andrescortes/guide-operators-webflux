package com.co.ias.moviesinfoservice.repository;

import com.co.ias.moviesinfoservice.domain.MovieInfo;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        List<MovieInfo> movieInfos = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "Dark Night", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2008-06-15")),
            new MovieInfo(null, "Dark Night Rises", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2012-06-15"))
        );
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        // when
        Flux<MovieInfo> movieInfoRepositoryAll = movieInfoRepository.findAll();
        // then
        StepVerifier.create(movieInfoRepositoryAll)
            .expectNextCount(3)
            .verifyComplete();
    }
}
