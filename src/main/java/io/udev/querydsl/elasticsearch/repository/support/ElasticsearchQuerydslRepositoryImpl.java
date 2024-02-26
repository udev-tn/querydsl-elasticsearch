package io.udev.querydsl.elasticsearch.repository.support;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import io.udev.querydsl.elasticsearch.ElasticsearchQuery;
import io.udev.querydsl.elasticsearch.repository.ElasticsearchExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.function.Function;

import static io.udev.querydsl.elasticsearch.repository.utils.QuerydslUtils.toQueryDslOrders;

/**
 * Provides the implementation for an Elasticsearch repository.
 *
 * @param <T>  The type of the entity.
 * @param <ID> The type of the entity's identifier.
 */
class ElasticsearchQuerydslRepositoryImpl<T, ID>
        extends SimpleElasticsearchRepository<T, ID>
        implements ElasticsearchExecutor<T, ID> {
    protected final ElasticsearchOperations operations;
    protected final ElasticsearchQuery<T, ?> esQuery;

    public ElasticsearchQuerydslRepositoryImpl(ElasticsearchEntityInformation<T, ID> entityInformation, ElasticsearchOperations operations) {
        super(entityInformation, operations);

        this.entityInformation = entityInformation;
        this.operations = operations;
        this.esQuery = new ElasticsearchQuery<>(operations, entityClass);
    }


    @NonNull
    @Override
    public Optional<T> findOne(@NonNull Predicate predicate) {
        return Optional.ofNullable(esQuery.where(predicate).fetchOne());
    }

    @NonNull
    @Override
    public Iterable<T> findAll(@NonNull Predicate predicate) {
        return findAll(predicate, (Sort) null);
    }

    @NonNull
    @Override
    public Iterable<T> findAll(@NonNull Predicate predicate, @Nullable Sort sort) {
        return findAll(predicate, toQueryDslOrders(entityClass, sort));
    }

    @NonNull
    @Override
    public Iterable<T> findAll(@NonNull Predicate predicate, @NonNull OrderSpecifier<?>... orders) {
        return esQuery.where(predicate).orderBy(orders).fetch();
    }

    @NonNull
    @Override
    public Iterable<T> findAll(@NonNull OrderSpecifier<?>... orders) {
        return esQuery.orderBy(orders).fetch();
    }

    @NonNull
    @Override
    public Page<T> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        ElasticsearchQuery<T, ?> query = esQuery.where(predicate)
                .orderBy(toQueryDslOrders(entityClass, pageable.getSort()));
        if (pageable.isPaged()) {
            query.offset((long) pageable.getPageNumber() * pageable.getPageSize())
                    .limit(pageable.getPageSize());
        }

        return new PageImpl<>(esQuery.fetch(), pageable, count(predicate));
    }

    @Override
    public long count(@NonNull Predicate predicate) {
        return esQuery.where(predicate).fetchCount();
    }

    @Override
    public boolean exists(@NonNull Predicate predicate) {
        return count(predicate) > 0;
    }

    @NonNull
    @Override
    public <S extends T, R> R findBy(@NonNull Predicate predicate, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("FluentQuery not supported");
    }
}
