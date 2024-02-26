package io.udev.querydsl.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Elasticsearch specific {@link org.springframework.data.repository.Repository} interface with reactive and querydsl support.
 *
 * @param <T>  projection type.
 * @param <ID> projection ID type.
 */
@NoRepositoryBean
public interface ReactiveElasticsearchExecutor<T, ID>
        extends ReactiveElasticsearchRepository<T, ID>, ReactiveQuerydslPredicateExecutor<T> {
}
