package io.udev.querydsl.elasticsearch;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.BaseQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import static org.springframework.data.elasticsearch.core.query.Query.DEFAULT_PAGE_SIZE;

/**
 * Provides common functionality for converting a querydsl predicate into a spring-data-elasticsearch query.
 *
 * @param <T> projection type
 * @param <Q> concrete subtype of querydsl
 */
public abstract class AbstractElasticsearchQuery<T, Q extends AbstractElasticsearchQuery<T, Q>>
        implements SimpleQuery<Q> {
    protected final QueryMixin<Q> queryMixin;
    protected final Class<T> entityClass;
    protected final ElasticsearchSerializer serializer;

    @SuppressWarnings("unchecked")
    public AbstractElasticsearchQuery(Class<T> entityClass, ElasticsearchSerializer serializer) {
        this.entityClass = entityClass;
        this.serializer = serializer;

        queryMixin = new QueryMixin<>((Q) this);
    }

    public AbstractElasticsearchQuery(Class<T> entityClass) {
        this(entityClass, ElasticsearchSerializer.DEFAULT);
    }

    /**
     * Builds the Elasticsearch query based on the specified criteria.
     *
     * @return Returns the built Elasticsearch query.
     */
    protected final Query buildQuery() {
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
