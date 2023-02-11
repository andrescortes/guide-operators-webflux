package com.learnreactiveprogramming.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
            .expectNext("alex", "bell", "chloe")
            .verifyComplete();

        StepVerifier.create(namesFlux)
            .expectNext("alex", "bell")
            .expectNextCount(1)
            .verifyComplete();

        StepVerifier.create(namesFlux)
            .expectNext("alex")
            .expectNextCount(2)
            .verifyComplete();

        StepVerifier.create(namesFlux)
            .expectNext("alex", "bell", "chloe")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test()
    void namesFluxMap() {
        //given
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxMap();
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("ALEX", "BELL", "CHLOE")
            .expectNextCount(0)
            .verifyComplete();
    }


    @Test
    void namesFluxInmutability() {
        //given
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxInmutability();
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("alex", "bell", "chloe")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void namesFluxFilterByLength() {
        //given
        int stringLength = 3;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxFilterByLength(
            stringLength);
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("ALEX", "CHLOE")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void namesFluxFlatMapFilterByLength() {
        //given
        int stringLength = 3;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxMapFilterByLength(
            stringLength);
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("4-ALEX", "5-CHLOE")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void main() {
    }

    @Test
    void splitString() {
    }

    @Test
    void splitStringWithDelay() {
    }

    @Test
    void nameMono() {
    }

    @Test
    void namesFluxMapFilterByLength() {
        //given
        int stringLength = 3;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxFlatMapFilterByLength(
            stringLength).log("item:");
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNextCount(0)
            .verifyComplete();

    }

    @Test
    void namesFluxFlatMapAsync() {
        //given
        int stringLength = 3;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength)
            .log("item:");
        //then
        StepVerifier.create(namesFluxMap)
//            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNextCount(9)
            .verifyComplete();
    }

    @Test
    void namesConcatMap() {
        //given
        int stringLength = 3;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesConcatMap(stringLength)
            .log("item:");
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void nameMonoFlatMap() {
        // given
        int stringLength = 3;
        // when
        Mono<List<String>> listMono = fluxAndMonoGeneratorService.nameMonoFlatMap(stringLength)
            .log();
        // then
        StepVerifier.create(listMono)
            .expectNext(List.of("A", "L", "E", "X"))
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void nameMonoFlatMapMany() {
        // given
        int stringLength = 3;
        // when
        Flux<String> stringFlux = fluxAndMonoGeneratorService.nameMonoFlatMapMany(stringLength)
            .log();
        // then
        StepVerifier.create(stringFlux)
            .expectNext("A", "L", "E", "X")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        //given
        int stringLength = 3;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxTransform(stringLength)
            .log("item:");
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void namesFluxTransformDefaultEmpty() {
        //given
        int stringLength = 6;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxTransformDefaultEmpty(
                stringLength)
            .log("item:");
        //then
        StepVerifier.create(namesFluxMap)
//            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            //.expectNextCount(0)
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        //given
        int stringLength = 6;
        //when
        Flux<String> namesFluxMap = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(
                stringLength)
            .log("item:");
        //then
        StepVerifier.create(namesFluxMap)
            .expectNext("D", "E", "F", "A", "U", "L", "T")
            .verifyComplete();
    }

    @Test
    void exploreConcat() {
        //given

        //when
        Flux<String> concatFlux = fluxAndMonoGeneratorService.exploreConcat()
            .log("item:");
        //then
        StepVerifier.create(concatFlux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        //given

        //when
        Flux<String> concatFlux = fluxAndMonoGeneratorService.exploreConcatWith()
            .log("item:");
        //then
        StepVerifier.create(concatFlux)
            .expectNext("A", "B")
            .verifyComplete();
    }

    @Test
    void exploreMerge() {
        //when
        Flux<String> concatFlux = fluxAndMonoGeneratorService.exploreMerge()
            .log("item:");

        //then
        StepVerifier.create(concatFlux)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        //when
        Flux<String> concatFlux = fluxAndMonoGeneratorService.exploreMergeWith()
            .log("item:");

        //then
        StepVerifier.create(concatFlux)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {
        //when
        Flux<String> concatFlux = fluxAndMonoGeneratorService.exploreMergeWithMono()
            .log("item:");

        //then
        StepVerifier.create(concatFlux)
            .expectNext("A", "B")
            .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        //when
        Flux<String> value = fluxAndMonoGeneratorService.exploreMergeSequential()
            .log("item:");

        //then
        StepVerifier.create(value)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void exploreZip() {
        //when
        Flux<String> value = fluxAndMonoGeneratorService.exploreZip()
            .log("item:");

        //then
        StepVerifier.create(value)
            .expectNext("AD", "BE", "CF")
            .verifyComplete();
    }

    @Test
    void exploreZip2() {
        //when
        Flux<String> value = fluxAndMonoGeneratorService.exploreZip2()
            .log("item:");

        //then
        StepVerifier.create(value)
            .expectNext("AD14", "BE25", "CF36")
            .verifyComplete();
    }

    @Test
    void exploreZipWith() {
        //when
        Flux<String> value = fluxAndMonoGeneratorService.exploreZipWith()
            .log("item:");

        //then
        StepVerifier.create(value)
            .expectNext("AD", "BE", "CF")
            .verifyComplete();
    }

    @Test
    void exploreMergeZipWithMono() {
        //when
        Mono<String> value = fluxAndMonoGeneratorService.exploreMergeZipWithMono()
            .log("item:");

        //then
        StepVerifier.create(value)
            .expectNext("AB")
            .verifyComplete();
    }
}
