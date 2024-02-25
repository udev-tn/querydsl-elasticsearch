package io.udev.querydsl.elasticsearch;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Elasticsearch query implementations.
 *
 * @param <T> projection type
 * @param <Q> concrete subtype of querydsl
 */
public class ElasticsearchQuery<T, Q extends ElasticsearchQuery<T, Q>>
        extends AbstractElasticsearchQuery<T, Q>
        implements Fetchable<T> {
    private final ElasticsearchOperations operations;

    public ElasticsearchQuery(ElasticsearchOperations operations, Class<T> entityClass, ElasticsearchSerializer serializer) {
        super(entityClass, serializer);
        this.operations = operations;
    }

    public ElasticsearchQuery(ElasticsearchOperations operations, Class<T> entityClass) {
        super(entityClass);
        this.operations = operations;
    }

    /**
     * Executes the search operation and returns a stream of results.
     *
     * @return Returns a stream of documents that match the query.
     */
    private Stream<T> doSearch() {
        return operations.search(buildQuery(), entityClass)
                .map(SearchHit::getContent)
                .stream();
    }

    @Override
    public List<T> fetch() {
        return doSearch().collect(toList());
    }

    @Override
    public @Nullable T fetchFirst() {
        // Reduce documents
        queryMixin.limit(1);

        return doSearch()
                .findFirst()
                .orElse(null);
    }

    @Override
    public @Nullable T fetchOne() throws NonUniqueResultException {
        return fetchFirst();
    }

    @Override
    public CloseableIterator<T> iterate() {
        return new IteratorAdapter<>(doSearch().iterator());
    }

    @Override
    public QueryResults<T> fetchResults() {
        QueryModifiers modifiers = queryMixin.getMetadata().getModifiers();

        return new QueryResults<>(fetch(), modifiers, fetchCount());
    }

    @Override
    public long fetchCount() {
        return operations.count(buildQuery(), entityClass);
    }
}
