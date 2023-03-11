package com.co.ias.moviesinfoservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;

import com.co.ias.moviesinfoservice.domain.MovieInfo;
import com.co.ias.moviesinfoservice.service.MoviesInfoService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {

    private final String URL = "/v1/movieinfos";
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private MoviesInfoService moviesInfoServiceMock;
    private List<MovieInfo> movieInfos;
    private MovieInfo movieInfo;
    private MovieInfo movieInfo2;

    @BeforeEach
    void setUp() {
        movieInfo = MovieInfo.builder()
            .movieInfoId("111")
            .name("Barcelona")
            .year(2015)
            .releaseDate(LocalDate.now().atStartOfDay())
            .cast(List.of("Wolf", "Cow"))
            .build();
        movieInfo2 = MovieInfo.builder()
            .name("Zoo")
            .movieInfoId("222")
            .year(2016)
            .releaseDate(LocalDate.now().atStartOfDay())
            .cast(List.of("Silver", "Plate"))
            .build();

        movieInfos = List.of(movieInfo, movieInfo2);
    }

    @Test
    void shouldGetAllMovieInfos() {
        // given
        // when
        Mockito.when(moviesInfoServiceMock.getAllMovieInfos())
            .thenReturn(Flux.fromIterable(movieInfos));
        // then
        WebTestClient.ResponseSpec spec = webTestClient
            .get()
            .uri(URL)
            .exchange();
            /*.expectStatus().is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(1);*/
        spec.expectBodyList(MovieInfo.class)
            .consumeWith(res -> {
                List<MovieInfo> responseBody = res.getResponseBody();
                Assertions.assertNotNull(responseBody);
                Assertions.assertEquals(2, responseBody.size());
                Assertions.assertEquals(movieInfo, responseBody.get(0));
                Assertions.assertEquals(movieInfo2, responseBody.get(1));
            });

    }

    @Test
    void shouldGetMovieInfoById() {
        // given
        String movieInfoId = "111";
        // when
        Mockito.when(moviesInfoServiceMock.getMovieInfoById(movieInfoId))
            .thenReturn(Mono.just(movieInfo));
        // then
        ResponseSpec exchange = webTestClient.get()
            .uri(URL + "/" + movieInfoId)
            .exchange()
            .expectStatus().is2xxSuccessful();

        exchange.expectBody(MovieInfo.class)
            .isEqualTo(movieInfo);

    }

    @Test
    void shouldDeleteMovieInfo() {
        // given
        String movieInfoId = "111";
        // when
        Mockito.when(moviesInfoServiceMock.deleteMovieInfo(movieInfoId));
        // then
        webTestClient.delete()
            .uri(URL + "/" + movieInfoId)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(Void.class);
    }

    @Test
    void shouldAddMovieInfo() {
        // given
        // when
        Mockito.when(moviesInfoServiceMock.addMovieInfo(any())).thenReturn(Mono.just(movieInfo));
        // then
        ResponseSpec exchange = webTestClient.post()
            .uri(URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus().isCreated();

        exchange.expectBody(MovieInfo.class)
            .isEqualTo(movieInfo);
    }

    @Test
    void shouldAddMovieInfoAttrNotNull() {
        // given
        MovieInfo movieInfoValid = MovieInfo.builder()
            .movieInfoId("123")
            .name("")
            .year(-2000)
            .cast(List.of("Talk", "Closing"))
            .releaseDate(LocalDate.now().atStartOfDay())
            .build();
        // when
        Mockito.when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
            .thenReturn(Mono.just(movieInfoValid));
        // then
        webTestClient.post()
            .uri(URL)
            .bodyValue(movieInfoValid)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldAddMovieInfoValidation() {
        // given
        MovieInfo movieInfoValid = MovieInfo.builder()
            .movieInfoId("123")
            .name("")
            .year(-2000)
            .cast(List.of(""))
            .releaseDate(LocalDate.now().atStartOfDay())
            .build();
        // when
        Mockito.when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
            .thenReturn(Mono.just(movieInfoValid));
        // then
        webTestClient.post()
            .uri(URL)
            .bodyValue(movieInfoValid)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(List.class)
            .consumeWith(res -> {
                List responseBody = res.getResponseBody();
                assert responseBody != null;
                String castValid = "movieInfo.cast must be present";
                String nameValid = "movieInfo.name must be present";
                String yearValid = "movieInfo.year element must be a strictly positive number >= 0";
                Assertions.assertEquals(castValid, responseBody.get(0));
                Assertions.assertEquals(nameValid, responseBody.get(1));
                Assertions.assertEquals(yearValid, responseBody.get(2));
                Assertions.assertEquals(3, responseBody.size());
            });
    }

    @Test
    void shouldUpdateMovieInfo() {
        // given
        String id = "111";
        // when
        Mockito.when(moviesInfoServiceMock.updateMovieInfo(any(), any()))
            .thenReturn(Mono.just(movieInfo));
        // then
        ResponseSpec exchange = webTestClient.put()
            .uri(URL + "/" + id)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus().isOk();

        exchange.expectBody(MovieInfo.class)
            .isEqualTo(movieInfo);
    }

    @Test
    void shouldGetMovieInfoByYear() {
        // given
        Integer year = 2015;
        URI uri = UriComponentsBuilder.fromUriString(URL)
            .queryParam("year", year)
            .buildAndExpand().toUri();
        // when
        Mockito.when(moviesInfoServiceMock.getMovieInfosByYear(year))
            .thenReturn(Flux.fromIterable(movieInfos));
        // then
        ResponseSpec exchange = webTestClient
            .get()
            .uri(uri)
            .exchange();

        exchange.expectBodyList(MovieInfo.class)
            .consumeWith(res -> {
                List<MovieInfo> responseBody = res.getResponseBody();
                HttpStatus status = res.getStatus();
                Assertions.assertNotNull(responseBody);
                Assertions.assertTrue(status.is2xxSuccessful());
                Assertions.assertEquals(2, responseBody.size());
                Assertions.assertEquals(movieInfo, responseBody.get(0));
            });
    }
}
