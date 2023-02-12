package com.co.ias.moviesinfoservice.controller;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        final WebTestClient.ResponseSpec spec = webTestClient.get()
            .uri("/flux")
            .exchange();

        spec.expectBodyList(Integer.class)
            .hasSize(4)
            .contains(1, 2, 3, 4)
            .consumeWith(res -> {
                List<Integer> integerList = res.getResponseBody();
                Assertions.assertEquals(1, integerList.get(0));
                Assertions.assertEquals(2, (int) integerList.get(1));
            });
    }

    @Test
    void fluxApproach1() {
        webTestClient.get()
            .uri("/flux")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBodyList(Integer.class)
            .hasSize(4);
    }

    @Test
    void fluxApproach2() {
        Flux<Integer> responseBody = webTestClient.get()
            .uri("/flux")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .returnResult(Integer.class)
            .getResponseBody().log("item: ");

        StepVerifier.create(responseBody)
            .consumeNextWith(r -> Assertions.assertEquals(1, r))
            .consumeNextWith(r -> Assertions.assertEquals(2, r))
            .consumeNextWith(r -> Assertions.assertEquals(3, r))
            .consumeNextWith(r -> Assertions.assertEquals(4, r))
            .verifyComplete();
    }

    @Test
    void mono() {
        webTestClient
            .get()
            .uri("/mono")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(String.class)
            .isEqualTo("Hello-World!");
    }

    @Test
    void mono2() {
        webTestClient
            .get()
            .uri("/mono")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(String.class)
            .consumeWith(r -> {
                String responseBody = r.getResponseBody();
                Assertions.assertEquals("Hello-World!", responseBody);
            });
    }

    @Test
    void stream() {
        Flux<Long> responseBody = webTestClient
            .get()
            .uri("/stream")
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Long.class)
            .getResponseBody();

        StepVerifier.create(responseBody)
            .expectNext(0L, 1L, 2L, 3L)
            .thenCancel()
            .verify();
    }
}
