package io.udev.querydsl.elasticsearch;

import com.querydsl.core.NonUniqueResultException;
import io.udev.querydsl.elasticsearch.repository.ReactiveFetchable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive implementations for Elasticsearch querying.
 *
 * @param <T> projection type
 * @param <Q> concrete subtype of querydsl
 */
public class ReactiveElasticsearchQuery<T, Q extends ElasticsearchQuery<T, Q>>
        extends AbstractElasticsearchQuery<T, Q>
        implements ReactiveFetchable<T> {
    private final ReactiveElasticsearchOperations operations;

    public ReactiveElasticsearchQuery(ReactiveElasticsearchOperations operations, Class<T> entityClass, ElasticsearchSerializer serializer) {
        super(entityClass, serializer);
        this.operations = operations;
    }

    public ReactiveElasticsearchQuery(ReactiveElasticsearchOperations operations, Class<T> entityClass) {
        super(entityClass);
        this.operations = operations;
    }

    /**
     * Performs a search operation and returns a Flux of search results.
     *
     * @return {@link Flux} emitting all entities.
     */
    private Flux<T> doSearch() {
        return operations.search(buildQuery(), entityClass)
                .map(SearchHit::getContent);
    }

    @Override
    public Flux<T> fetch() {
        return doSearch();
    }

    @Override
    public Mono<T> fetchFirst() {
        // Reduce documents
        queryMixin.limit(1);

        return doSearch().singleOrEmpty();
    }

    @Override
    public Mono<T> fetchOne() throws NonUniqueResultException {
        return fetchFirst();
    }

    @Override
    public Mono<Long> fetchCount() {
        return operations.count(buildQuery(), entityClass);
    }
}
