package com.co.ias.moviesinfoservice.repository;

import com.co.ias.moviesinfoservice.domain.MovieInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    // https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo/issues/395#issuecomment-1257032064
    static {
        System.setProperty("spring.mongodb.embedded.version", "5.0.0");
    }

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        List<MovieInfo> movieInfos = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "Dark Night", 2005, List.of("Christian Bale", "Michael Cane"),
                LocalDate.parse("2008-06-15")),
            new MovieInfo("abc", "Dark Night Rises", 2005,
                List.of("Christian Bale", "Michael Cane"),
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
        Flux<MovieInfo> movieInfoRepositoryAll = movieInfoRepository.findAll().log();
        // then
        StepVerifier.create(movieInfoRepositoryAll)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void shouldFindById() {
        // when
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findById("abc").log();
        // then
        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                Assertions.assertEquals("Dark Night Rises", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void shouldSaveMovieInfo() {
        // given
        MovieInfo movieInfoToSave = new MovieInfo(null, "Batman Begins", 2005,
            List.of("Christian Bale", "Michael Cane"),
            LocalDate.parse("2005-06-15"));
        // when
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.save(movieInfoToSave).log();
        // then
        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                Assertions.assertEquals("Batman Begins", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void shouldUpdateMovieInfo() {
        // given
        MovieInfo movieInfoMonoToUpdate = movieInfoRepository.findById("abc").block();
        movieInfoMonoToUpdate.setYear(2021);

        // when
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.save(movieInfoMonoToUpdate).log();
        // then
        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                Assertions.assertEquals(2021, movieInfo.getYear());
            })
            .verifyComplete();
    }

    @Test
    void shouldDeleteMovieInfo() {
        // given

        // when
        movieInfoRepository.deleteById("abc").block();
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll().log();
        // then
        StepVerifier.create(movieInfoFlux)
            .expectNextCount(2)
            .verifyComplete();
    }
}
