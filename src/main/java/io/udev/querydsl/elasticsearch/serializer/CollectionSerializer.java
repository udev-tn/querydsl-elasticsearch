package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * Prepare a {@link Criteria} and a {@link Collection} of values to perform a collection operation.
 * This can be used for operations like the {@link Ops#IN} operator.
 */
class CollectionSerializer extends OperationSerializer {
    /**
     * A consumer that can be used to perform a collection operation.
     */
    private final BiConsumer<Criteria, Collection<?>> criteriaConsumer;

    public CollectionSerializer(BiConsumer<Criteria, Collection<?>> criteriaConsumer) {
        super();

        this.criteriaConsumer = criteriaConsumer;
    }

    public CollectionSerializer(BiConsumer<Criteria, Collection<?>> criteriaConsumer, UnaryOperator<Criteria> postOperator) {
        super(postOperator);

        this.criteriaConsumer = criteriaConsumer;
    }

    @NonNull
    @Override
    public Criteria accept(@NonNull Operation<?> input, QueryMetadata metadata) {
        Path<?> path = getPath(input.getArg(0));
        String field = toField(path);

        @SuppressWarnings("unchecked")
        Constant<Collection<?>> expectedConstant = (Constant<Collection<?>>) input.getArg(1);
        Collection<?> values = expectedConstant.getConstant();

        Criteria criteria = Criteria.where(field);
        criteriaConsumer.accept(criteria, values);
        if (postOperator != null) {
            postOperator.apply(criteria);
        }

        return criteria;
    }
}
