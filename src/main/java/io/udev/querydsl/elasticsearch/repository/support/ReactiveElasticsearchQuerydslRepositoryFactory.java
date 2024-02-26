package io.udev.querydsl.elasticsearch.repository.support;

import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ReactiveElasticsearchRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.lang.NonNull;

/**
 * Custom Spring Data Elasticsearch Reactive Repository Factory.
 * This class is responsible for creating reactive Elasticsearch repositories.
 */
class ReactiveElasticsearchQuerydslRepositoryFactory extends ReactiveElasticsearchRepositoryFactory {

    /**
     * Creates a new {@link ReactiveElasticsearchQuerydslRepositoryFactory} with the given
     * {@link ReactiveElasticsearchOperations}.
     *
     * @param elasticsearchOperations must not be {@literal null}.
     */
    public ReactiveElasticsearchQuerydslRepositoryFactory(ReactiveElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @NonNull
    @Override
    protected Class<?> getRepositoryBaseClass(@NonNull RepositoryMetadata metadata) {
        return ReactiveElasticsearchQuerydslRepositoryImpl.class;
    }
}
