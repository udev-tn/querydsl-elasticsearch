package io.udev.querydsl.elasticsearch.repository.support;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.lang.NonNull;

/**
 * Custom Spring Data Elasticsearch Repository Factory.
 * This class is responsible for creating Elasticsearch repositories.
 */
class ElasticsearchQuerydslRepositoryFactory extends ElasticsearchRepositoryFactory {

    /**
     * Creates a new {@link ElasticsearchQuerydslRepositoryFactory} with the given
     * {@link ElasticsearchOperations}.
     *
     * @param elasticsearchOperations must not be {@literal null}.
     */
    public ElasticsearchQuerydslRepositoryFactory(ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @NonNull
    @Override
    protected Class<?> getRepositoryBaseClass(@NonNull RepositoryMetadata metadata) {
        return ElasticsearchQuerydslRepositoryImpl.class;
    }
}
