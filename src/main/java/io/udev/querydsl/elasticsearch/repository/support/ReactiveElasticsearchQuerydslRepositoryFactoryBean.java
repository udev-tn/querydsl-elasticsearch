package io.udev.querydsl.elasticsearch.repository.support;

import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ReactiveElasticsearchRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.NonNull;

/**
 * A custom factory bean class that extends Spring Data Elasticsearch's ReactiveRepositoryFactoryBean.
 * <p>
 * This class is used to create Elasticsearch repositories in a reactive way.
 *
 * @param <T>  The repository type.
 * @param <S>  The entity type.
 * @param <ID> The type of the entity's ID.
 */
public class ReactiveElasticsearchQuerydslRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
        extends ReactiveElasticsearchRepositoryFactoryBean<T, S, ID> {


    /**
     * Creates a new {@link ReactiveElasticsearchQuerydslRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public ReactiveElasticsearchQuerydslRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @NonNull
    @Override
    protected RepositoryFactorySupport getFactoryInstance(@NonNull ReactiveElasticsearchOperations operations) {
        return new ReactiveElasticsearchQuerydslRepositoryFactory(operations);
    }
}
