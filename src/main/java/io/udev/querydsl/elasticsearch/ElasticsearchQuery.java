package io.udev.querydsl.elasticsearch;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.BaseQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.elasticsearch.core.query.Query.DEFAULT_PAGE_SIZE;

/**
 * Elasticsearch query implementations.
 *
 * @param <T> projection type
 * @param <Q> concrete subtype of querydsl
 */
public class ElasticsearchQuery<T, Q extends ElasticsearchQuery<T, Q>>
        implements SimpleQuery<Q>, Fetchable<T> {
    private final QueryMixin<Q> queryMixin;
    private final ElasticsearchOperations operations;
    private final Class<T> entityClass;
    private final ElasticsearchSerializer serializer;

    @SuppressWarnings("unchecked")
    public ElasticsearchQuery(ElasticsearchOperations operations, Class<T> entityClass, ElasticsearchSerializer serializer) {
        this.operations = operations;
        this.entityClass = entityClass;
        this.serializer = serializer;

        queryMixin = new QueryMixin<>((Q) this);
    }

    public ElasticsearchQuery(ElasticsearchOperations operations, Class<T> entityClass) {
        this(operations, entityClass, ElasticsearchSerializer.DEFAULT);
    }

    /**
     * Builds the Elasticsearch query based on the specified criteria.
     *
     * @return Returns the built Elasticsearch query.
     */
    private Query buildQuery() {
        BaseQuery query = (BaseQuery) Query.findAll();
        QueryMetadata metadata = queryMixin.getMetadata();
        QueryModifiers modifiers = metadata.getModifiers();

        int offset = 0;
        int limit = DEFAULT_PAGE_SIZE;
        if (modifiers.getLimit() != null) {
            limit = modifiers.getLimitAsInteger();
        }
        if (modifiers.getOffset() != null) {
            offset = modifiers.getOffsetAsInteger();
        }
        query.setPageable(PageRequest.of(offset / limit, limit, serializer.toSort(metadata.getOrderBy())));

        if (metadata.getWhere() != null) {
            query = new CriteriaQuery(serializer.toCriteria(metadata.getWhere(), metadata), query.getPageable());
        }

        return query;
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
        return null;
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

    @Override
    public Q limit(@Range(from = 0L, to = 2147483647L) long limit) {
        return queryMixin.limit(limit);
    }

    @Override
    public Q offset(@Range(from = 0L, to = 2147483647L) long offset) {
        return queryMixin.offset(offset);
    }

    @Override
    public Q restrict(QueryModifiers modifiers) {
        return queryMixin.restrict(modifiers);
    }

    @Override
    public Q orderBy(OrderSpecifier<?>... o) {
        return queryMixin.orderBy(o);
    }

    @Override
    public <ParamType> Q set(ParamExpression<ParamType> param, ParamType value) {
        return queryMixin.set(param, value);
    }

    @Override
    public Q distinct() {
        return queryMixin.distinct();
    }

    @Override
    public Q where(Predicate... o) {
        return queryMixin.where(o);
    }
}
