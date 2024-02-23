package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;

import java.util.function.UnaryOperator;

/**
 * Prepare a {@link Criteria} and extract values to carry out an existence operation.
 */
class ExistsSerializer extends OperationSerializer {
    public ExistsSerializer() {
    }

    public ExistsSerializer(UnaryOperator<Criteria> postOperator) {
        super(postOperator);
    }

    @NonNull
    @Override
    public Criteria accept(@NonNull Operation<?> input, QueryMetadata metadata) {
        Path<?> path = getPath(input.getArg(0));

        Criteria criteria = Criteria.where(toField(path)).exists();
        if (postOperator != null) {
            postOperator.apply(criteria);
        }
        return criteria;
    }
}
