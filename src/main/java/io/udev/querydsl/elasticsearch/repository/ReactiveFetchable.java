package io.udev.querydsl.elasticsearch.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@code ReactiveFetchable} defines default projection methods for {@link Query} implementations that adhere to reactive paradigms.
 * It uses Project Reactor types, which are based on Reactive Streams.
 * All Querydsl query implementations should implement this interface.
 *
 * @param <T> result type
 */
public interface ReactiveFetchable<T> {
    /**
     * Fetches all results of the query as a {@link Flux}.
     *
     * @return A {@link Flux} emitting all results of the query.
     */
    Flux<T> fetch();

    /**
     * Get the first result or {@link Mono#empty()} if none was found.
     *
     * @return A {@link Mono} emitting the first result of the query or {@link Mono#empty()} if none was found.
     */
    Mono<T> fetchFirst();

    /**
     * Fetches a single result of the query as a {@link Mono} or {@link Mono#empty()} if none was found.
     *
     * @return A {@link Mono} emitting the single result of the query or {@link Mono#empty()} if none was found.
     * @throws NonUniqueResultException if the query returns more than one result.
     */
    Mono<T> fetchOne() throws NonUniqueResultException;

    /**
     * Returns a {@link Mono} emitting the number of instances.
     *
     * @return a {@link Mono} emitting the number of instances or {@code 0} if none found.
     */
    Mono<Long> fetchCount();
}
