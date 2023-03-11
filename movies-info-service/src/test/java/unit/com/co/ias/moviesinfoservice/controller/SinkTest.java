package com.co.ias.moviesinfoservice.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;

class SinkTest {

    @Test
    void sinkMany() {
        // given
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        // when
        replaySink.emitNext(1, EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, EmitFailureHandler.FAIL_FAST);

        // then
        Flux<Integer> integerFlux = replaySink.asFlux();

        integerFlux.subscribe(integer -> {
            System.out.println("integer = " + integer);
        });

        Flux<Integer> integerFlux2 = replaySink.asFlux();

        integerFlux2.subscribe(integer -> {
            System.out.println("integer2 = " + integer);
        });

        replaySink.tryEmitNext(3);
        Assertions.assertTrue(true);
    }

    @Test
    void sinksMulticast() {
        //given
        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();

        //when
        multicast.emitNext(1, EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, EmitFailureHandler.FAIL_FAST);

        //then
        Flux<Integer> integerFlux = multicast.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber-1 = " + i);
        });

        Flux<Integer> integerFlux2 = multicast.asFlux();
        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber-2 = " + i);
        });

        multicast.emitNext(3, EmitFailureHandler.FAIL_FAST);

        Assertions.assertTrue(true);
    }
}
