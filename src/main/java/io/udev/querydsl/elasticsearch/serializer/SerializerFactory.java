package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Providing serializers for different operators.
 */
public class SerializerFactory {
    private static Map<Operator, ISerializer<Operation<?>, Criteria>> SERIALIZERS = new HashMap<>();

    private SerializerFactory() {
    }

    /**
     * Retrieves the serializer for the specified operator.
     *
     * @param operator The operator for which the serializer is required.
     * @return Returns the serializer for the specified operator.
     * @throws UnsupportedOperationException if the specified operator does not have a serializer.
     */
    public static ISerializer<Operation<?>, Criteria> getSerializer(@NonNull final Operator operator) {
        if (!SERIALIZERS.containsKey(operator)) {
            throw new UnsupportedOperationException("Illegal operation " + operator);
        }

        return SERIALIZERS.get(operator);
    }
}
