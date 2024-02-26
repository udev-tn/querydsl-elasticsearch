package io.udev.querydsl.elasticsearch.repository.support;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import io.udev.querydsl.elasticsearch.ReactiveElasticsearchQuery;
import io.udev.querydsl.elasticsearch.repository.ReactiveElasticsearchExecutor;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleReactiveElasticsearchRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static io.udev.querydsl.elasticsearch.repository.utils.QuerydslUtils.toQueryDslOrders;

/**
 * Provides the implementation for a reactive Elasticsearch repository.
 *
 * @param <T>  The type of the entity.
 * @param <ID> The type of the entity's identifier.
 */
class ReactiveElasticsearchQuerydslRepositoryImpl<T, ID>
        extends SimpleReactiveElasticsearchRepository<T, ID>
        implements ReactiveElasticsearchExecutor<T, ID> {
    protected final ReactiveElasticsearchOperations operations;
    protected final ReactiveElasticsearchQuery<T, ?> esQuery;
    protected final Class<T> entityClass;

    public ReactiveElasticsearchQuerydslRepositoryImpl(ElasticsearchEntityInformation<T, ID> entityInformation, ReactiveElasticsearchOperations operations) {
        super(entityInformation, operations);

        this.operations = operations;
        this.entityClass = entityInformation.getJavaType();
        this.esQuery = new ReactiveElasticsearchQuery<>(operations, entityClass);
    }

    @NonNull
    @Override
    public Mono<T> findOne(@NonNull Predicate predicate) {
        return esQuery.where(predicate).fetchOne();
    }

    @NonNull
    @Override
    public Flux<T> findAll(@NonNull Predicate predicate) {
        return findAll(predicate, (Sort) null);
    }

    @NonNull
    @Override
    public Flux<T> findAll(@NonNull Predicate predicate, @Nullable Sort sort) {
        return findAll(predicate, toQueryDslOrders(entityClass, sort));
    }

    @NonNull
    @Override
    public Flux<T> findAll(@NonNull Predicate predicate, @NonNull OrderSpecifier<?>... orders) {
        return esQuery.where(predicate).orderBy(orders).fetch();
    }

    @NonNull
    @Override
    public Flux<T> findAll(@NonNull OrderSpecifier<?>... orders) {
        return esQuery.orderBy(orders).fetch();
    }

    @NonNull
    @Override
    public Mono<Long> count(@NonNull Predicate predicate) {
        return esQuery.where(predicate).fetchCount();
    }

    @NonNull
    @Override
    public Mono<Boolean> exists(@NonNull Predicate predicate) {
        return count(predicate).map(aLong -> aLong != null && aLong > 0);
    }

    @NonNull
    @Override
    public <S extends T, R, P extends Publisher<R>> P findBy(@NonNull Predicate predicate, @NonNull Function<FluentQuery.ReactiveFluentQuery<S>, P> queryFunction) {
        throw new UnsupportedOperationException("FluentQuery not supported");
    }
}
