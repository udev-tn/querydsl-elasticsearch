package io.udev.querydsl.elasticsearch.repository.support;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * A custom factory bean class that extends Spring Data Elasticsearch's RepositoryFactoryBean.
 * <p>
 * This class is used to create Elasticsearch repositories.
 *
 * @param <T>  The repository type.
 * @param <S>  The entity type.
 * @param <ID> The type of the entity's ID.
 */
public class ElasticsearchQuerydslRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends ElasticsearchRepositoryFactoryBean<T, S, ID> {
    private ElasticsearchOperations operations;

    /**
     * Creates a new {@link ElasticsearchQuerydslRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public ElasticsearchQuerydslRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    public void setOperations(@NonNull ElasticsearchOperations operations) {
        super.setElasticsearchOperations(operations);

        this.operations = operations;
    }

    @NonNull
    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new ElasticsearchQuerydslRepositoryFactory(operations);
    }
}
