package com.co.ias.reactive.repository.jpa;

import static reactor.core.publisher.Flux.defer;
import static reactor.core.publisher.Flux.fromIterable;
import static reactor.core.publisher.Mono.fromSupplier;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Log4j2
public abstract class AdapterOperationsJpa<E, D, I, R extends CrudRepository<D, I> & QueryByExampleExecutor<D>> {

    protected R repository;
    private Function<D, E> toEntityFn;
    private Function<E, D> toDataFn;

    /**
     * Este es el constructor, que se llamar\u00e1 como SUPER, pues una clase abstracta no puede ser
     * instanciada directamente
     *
     * @param repository Repositorio de datos ( JPA )
     * @param toEntityFn funci\u00f3n que transforma de dominio a entidad
     * @param toDataFn   funci\u00f3n que transforma de entidad a dominio
     */
    protected AdapterOperationsJpa(R repository, Function<D, E> toEntityFn, Function<E, D> toDataFn) {
        this.repository = repository;
        this.toEntityFn = toEntityFn;
        this.toDataFn = toDataFn;
    }

    /**
     * Esta funci\u00f3n permite guardar en el repo de datos, una entidad de dominio ( haciendo la
     * transformaci\u00f3n )
     *
     * @param entity Entidad de Dominio
     * @return Mono de Dominio
     */
    public Mono<E> save(E entity) {
        return Mono.just(entity)
            .map(this::toData)
            .flatMap(this::saveData)
            .map(this::toEntity);
    }

    /**
     * Este m\u00e9todo permite guardar un flujo de entidades de dominio en el repo de datos (
     * haciendo la transformaci\u00f3n )
     *
     * @param entities Flux de entidades de dominio a guardar
     * @return Flux de Dominio
     */
    protected Flux<E> saveAllEntities(Flux<E> entities) {
        return entities.map(this::toData).collectList()
            .flatMapMany(this::saveData).map(this::toEntity);
    }

    /**
     * Permite llamar una consulta del repo de tipo Mono ( que devuelve una entidad de datos ) y
     * devuelve un Mono de dominio ( haciendo la transformaci\u00f3n )
     *
     * @param query Mono de datos
     * @return Mono de dominio
     */
    private Mono<E> doQuery(Mono<D> query) {
        return query.map(this::toEntity);
    }

    /**
     * Permite llamar una consulta del repo de supplier y devuelve un Mono de dominio ( haciendo la
     * transformaci\u00f3n )
     *
     * @param query Supplier de datos
     * @return Mono de dominio
     */
    public Mono<E> doQuery(Supplier<D> query) {
        return doQuery(Mono.just(query.get())).subscribeOn(Schedulers.boundedElastic())
            .flatMap(Mono::justOrEmpty);
    }

    /**
     * Permite hacer una b\u00fasqueda por el ID y devuelve un Mono de Domino ( haciendo la
     * transformaci\u00f3n )
     *
     * @param id ID por el cual se buscar\u00e1
     * @return Mono de dominio
     */
    public Mono<E> findById(I id) {
        return doQuery(
            fromSupplier(() -> repository.findById(id)).subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty));
    }

    /**
     * Permite hacer una b\u00fasqueda por una entidad de dominio
     *
     * @param entity Entidad de dominio
     * @return Flujo de dominio
     */
    public Flux<E> findByExample(E entity) {
        return doQueryMany(() -> repository.findAll(Example.of(toData(entity))));
    }

    /**
     * Permite llamar una consulta del repo de supplier y devuelve un Flux de dominio ( haciendo la
     * transformaci\u00f3n )
     *
     * @param query supplier de datos
     * @return Flux de dominio
     */
    protected Flux<E> doQueryMany(Supplier<Iterable<D>> query) {
        return fromSupplier(query).subscribeOn(Schedulers.boundedElastic())
            .flatMapMany(Flux::fromIterable)
            .map(this::toEntity);
    }

    /**
     * Funci\u00f3n que funciona como wrapper de la funci\u00f3n que se recibe por contructor como
     * transformador de data a dominio
     *
     * @param entity Entidad de Domino
     * @return Entidad de Data
     */
    protected D toData(E entity) {
        log.info(toDataFn.toString());
        return toDataFn.apply(entity);
    }

    /**
     * Funci\u00f3n que funciona como wrapper de la funci\u00f3n que se recibe por contructor como
     * transformador de dominio a datos
     *
     * @param data Entidad de Datos
     * @return Entidad de Domonio
     */
    protected E toEntity(D data) {
        log.info(toEntityFn.toString());
        return toEntityFn.apply(data);
    }

    /**
     * Esta funci\u00f3n permite almacenar una entidad de datos y devolver un Mono del mismo
     *
     * @param data Entidad de datos
     * @return Mono de datos
     */
    protected Mono<D> saveData(D data) {
        return fromSupplier(() -> repository.save(data))
            .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Esta funci\u00f3n permite almacenar un flux de entidad de datos y devolver un Flux del mismo
     *
     * @param data lista de Entidad de datos
     * @return Flux de datos
     */
    protected Flux<D> saveData(Iterable<D> data) {
        log.info(data.toString());
        return defer(() -> fromIterable(repository.saveAll(data)))
            .subscribeOn(Schedulers.boundedElastic());
    }

}
