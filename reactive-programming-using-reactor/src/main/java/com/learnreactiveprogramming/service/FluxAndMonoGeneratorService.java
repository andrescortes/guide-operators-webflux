package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxAndMonoGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FluxAndMonoGeneratorService.class);

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
            .subscribe(name -> LOGGER.debug("name: {} ", name));
        fluxAndMonoGeneratorService.nameMono().subscribe(name -> LOGGER.debug("name: {}", name));
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "bell", "chloe")); // db or a remote service call
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("alex", "bell", "chloe"))
//            .map(item -> item.toUpperCase()); // db or a remote service call
            .map(String::toUpperCase); // db or a remote service call
    }

    public Flux<String> namesFluxInmutability() {
        Flux<String> stringFlux = Flux.fromIterable(List.of("alex", "bell", "chloe"));
        stringFlux.map(String::toUpperCase);
        return stringFlux;
    }

    public Flux<String> namesFluxFilterByLength(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .filter(item -> item.length() > stringLength)
            .map(String::toUpperCase); // db or a remote service call
    }

    public Flux<String> namesFluxMapFilterByLength(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
            .filter(item -> item.length() > stringLength)
            .map(s -> s.length() + "-" + s);

    }

    public Flux<String> namesFluxFlatMapFilterByLength(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
            .filter(item -> item.length() > stringLength)
            .flatMap(s -> splitString(s));
        // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E

    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
            .filter(item -> item.length() > stringLength)
            .flatMap(s -> splitStringWithDelay(s));
        // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
    }

    public Flux<String> namesConcatMap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
            .filter(item -> item.length() > stringLength)
            .concatMap(s -> splitStringWithDelay(s));
        // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
    }

    public Flux<String> splitString(String name) {
        String[] split = name.split("");
        return Flux.fromArray(split);
    }

    public Flux<String> splitStringWithDelay(String name) {
        String[] split = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(split).delayElements(Duration.ofMillis(delay));
    }

    public Mono<String> nameMono() {
        return Mono.just("Alex");
    }

    public Mono<List<String>> nameMonoFlatMap(int stringLength) {
        return Mono.just("Alex")
            .map(item -> item.toUpperCase())
            .filter(item -> item.length() > stringLength)
            .flatMap(item -> splitStringMono(item));//Mono<List> of A, L, E, X
    }

    public Flux<String> nameMonoFlatMapMany(int stringLength) {
        return Mono.just("Alex")
            .map(item -> item.toUpperCase())
            .filter(item -> item.length() > stringLength)
            .flatMapMany(item -> splitString(item));//Mono<List> of A, L, E, X
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .transform(filterMap)
            .flatMap(s -> splitString(s));
        // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
    }

    public Flux<String> namesFluxTransformDefaultEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .transform(filterMap)
            .flatMap(s -> splitString(s))
            .defaultIfEmpty("default");
        // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength)
            .flatMap(s -> splitString(s));

        Flux<String> stringFlux = Flux.just("default")
            .transform(filterMap);//"D","E","F","A","U","L","T"

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .transform(filterMap)
            .switchIfEmpty(stringFlux);
        // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
    }

    public Flux<String> exploreConcat() {
        Flux<String> stringFlux = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");
        return Flux.concat(stringFlux, stringFlux2);
    }

    public Flux<String> exploreConcatWith() {
        Mono<String> stringMonoA = Mono.just("A");
        Mono<String> stringMonoB = Mono.just("B");
        return stringMonoA.concatWith(stringMonoB);// A, B
    }


    public Flux<String> exploreMerge() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(120));
        return Flux.merge(stringFlux, stringFlux2);
    }

    public Flux<String> exploreMergeWith() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(120));
        return stringFlux.mergeWith(stringFlux2);
    }

    public Flux<String> exploreMergeWithMono() {
        Mono<String> stringMonoA = Mono.just("A");
        Mono<String> stringMonoB = Mono.just("B");
        return stringMonoA.mergeWith(stringMonoB);
    }


    public Flux<String> exploreMergeSequential() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(120));
        return stringFlux.mergeSequential(stringFlux, stringFlux2);
    }

    public Flux<String> exploreZip2() {
        Flux<String> stringFlux = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");
        Flux<String> _123Flux = Flux.just("1", "2", "3");
        Flux<String> _456Flux = Flux.just("4", "5", "6");
        return Flux.zip(stringFlux, stringFlux2, _123Flux, _456Flux)
            .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());
        //AD14, BE25, CF36
    }

    public Flux<String> exploreZip() {
        Flux<String> stringFlux = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");
        return Flux.zip(stringFlux, stringFlux2, (first, second) -> first + second);// AD, BE, CF
    }

    public Flux<String> exploreZipWith() {
        Flux<String> stringFlux = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");
        return stringFlux.zipWith(stringFlux2, (first, second) -> first + second); // AD, BE, CF
    }

    public Mono<String> exploreMergeZipWithMono(){
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");
        return aMono.zipWith(bMono)
            .map(t2 -> t2.getT1() + t2.getT2());// AB
    }


    private Mono<List<String>> splitStringMono(String s) {
        String[] split = s.split("");// ALEX -> A, L, E, X
        List<String> stringList = List.of(split);
        return Mono.just(stringList);
    }
}
