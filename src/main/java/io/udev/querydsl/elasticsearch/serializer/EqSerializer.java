package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;

import java.util.function.UnaryOperator;

/**
 * Prepare a {@link Criteria} and extract values to carry out an equality operation.
 */
class EqSerializer extends OperationSerializer {
    public EqSerializer() {
    }

    public EqSerializer(UnaryOperator<Criteria> postOperator) {
        super(postOperator);
    }

    @Override
    public @NonNull Criteria accept(@NonNull Operation<?> input, QueryMetadata metadata) {
        Path<?> path = getPath(input.getArg(0));
        String field = toField(path);
        String[] values = sanitizeValues(input.getArg(1), metadata);
        // TODO: Support Keywords
        Criteria criteria = Criteria.where(field).is(values[0]);
        if (postOperator != null) {
            postOperator.apply(criteria);
        }
        return criteria;
    }
}
