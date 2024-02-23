package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import io.udev.querydsl.elasticsearch.serializer.utils.ConsumeFirstArg;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Providing serializers for different operators.
 */
public class SerializerFactory {
    private static Map<Operator, ISerializer<Operation<?>, Criteria>> SERIALIZERS = new HashMap<>();

    static {
        // General
        SERIALIZERS.put(Ops.EQ, new EqSerializer());
        SERIALIZERS.put(Ops.NE, new EqSerializer(Criteria::not));
        SERIALIZERS.put(Ops.LIKE, new StringSerializer(ConsumeFirstArg.of(Criteria::contains)));
        SERIALIZERS.put(Ops.STRING_CONTAINS, new StringSerializer(ConsumeFirstArg.of(Criteria::contains)));
        SERIALIZERS.put(Ops.STARTS_WITH, new StringSerializer(ConsumeFirstArg.of(Criteria::startsWith)));
        SERIALIZERS.put(Ops.ENDS_WITH, new StringSerializer(ConsumeFirstArg.of(Criteria::endsWith)));
        // Math
        SERIALIZERS.put(Ops.LT, new MathSerializer(ConsumeFirstArg.of(Criteria::lessThan)));
        SERIALIZERS.put(Ops.GT, new MathSerializer(ConsumeFirstArg.of(Criteria::greaterThan)));
        SERIALIZERS.put(Ops.LOE, new MathSerializer(ConsumeFirstArg.of(Criteria::lessThanEqual)));
        SERIALIZERS.put(Ops.GOE, new MathSerializer(ConsumeFirstArg.of(Criteria::greaterThanEqual)));
        SERIALIZERS.put(Ops.BETWEEN, new MathSerializer((criteria, values) -> criteria.between(values.get(0), values.get(1))));
        // Null
        SERIALIZERS.put(Ops.EXISTS, new ExistsSerializer());
        SERIALIZERS.put(Ops.IS_NOT_NULL, new ExistsSerializer());
        SERIALIZERS.put(Ops.IS_NULL, new ExistsSerializer(Criteria::not));
        // Collection
        SERIALIZERS.put(Ops.IN, new CollectionSerializer(Criteria::in));
        SERIALIZERS.put(Ops.NOT_IN, new CollectionSerializer(Criteria::notIn));

        SERIALIZERS = unmodifiableMap(SERIALIZERS);
    }

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
