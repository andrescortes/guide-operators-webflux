package com.co.ias.moviesinfoservice.controller;

import java.time.Duration;
import lombok.extern.java.Log;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        log.warning("\n\n\n\nEnter endpoint /flux\n\n\n");
        return Flux.just(1, 2, 3, 4).log();
    }

    @GetMapping("/mono")
    public Mono<String> mono() {
        return Mono.just("Hello-World!").log();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream() {
        return Flux.interval(Duration.ofSeconds(1));
    }
}
