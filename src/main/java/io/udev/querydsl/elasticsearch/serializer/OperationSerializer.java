package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.types.Operation;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.function.UnaryOperator;

/**
 * Provides a base implementation for serializing operations with a generic type {@link Operation} and criteria with a generic type {@link Criteria}.
 */
abstract class OperationSerializer extends AbstractSerializer<Operation<?>, Criteria> {
    /**
     * A unary operator that can be applied after serializing the operation.
     */
    @Nullable
    protected UnaryOperator<Criteria> postOperator;

    public OperationSerializer() {
        super();
    }

    public OperationSerializer(@NonNull UnaryOperator<Criteria> postOperator) {
        this();

        this.postOperator = postOperator;
    }
}
