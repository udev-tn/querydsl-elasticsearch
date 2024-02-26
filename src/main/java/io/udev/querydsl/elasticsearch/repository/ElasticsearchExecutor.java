package io.udev.querydsl.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Elasticsearch specific {@link org.springframework.data.repository.Repository} interface with querydsl support.
 *
 * @param <T>  projection type.
 * @param <ID> projection ID type.
 */
@NoRepositoryBean
public interface ElasticsearchExecutor<T, ID>
        extends ElasticsearchRepository<T, ID>, QuerydslPredicateExecutor<T> {
}
