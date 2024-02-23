package io.udev.querydsl.elasticsearch;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import io.udev.querydsl.elasticsearch.serializer.SortSerializer;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.List;

import static io.udev.querydsl.elasticsearch.serializer.SerializerFactory.getSerializer;

/**
 * Serializes Querydsl queries to Elasticsearch queries.
 */
public class ElasticsearchSerializer {
    /**
     * The default instance of the ElasticsearchSerializer.
     */
    public static final ElasticsearchSerializer DEFAULT = new ElasticsearchSerializer();

    /**
     * Converts a Querydsl operation and query metadata into an Elasticsearch {@link Criteria}.
     *
     * @param operation The Querydsl operation to be converted.
     * @param metadata  The query metadata associated with the operation.
     * @return Returns the Elasticsearch {@link Criteria} representing the Querydsl operations.
     */
    private Criteria toCriteria(Operation<?> operation, QueryMetadata metadata) {
        Operator op = operation.getOperator();

        if (Ops.WRAPPED.equals(op)) {
            return toCriteria(operation.getArg(0), metadata);
        } else if (Ops.OR.equals(op)) {
            return toQueryChain(operation, Criteria.Operator.OR, metadata);
        } else if (Ops.AND.equals(op)) {
            return toQueryChain(operation, Criteria.Operator.AND, metadata);
        } else if (Ops.NOT.equals(op)) {
            return toCriteria(operation.getArg(0), metadata).not();
        }
        return getSerializer(op).accept(operation, metadata);
    }

    /**
     * Retrieve the Querydsl operations by using a boolean operator.
     *
     * @param operation The operation chain of Querydsl that needs to be converted.
     * @param operator  The boolean operator to be applied to the operation.
     * @param metadata  The query metadata associated with the operation.
     * @return Returns the Elasticsearch {@link Criteria} that represents the operation chain of the Querydsl.
     */
    private Criteria toQueryChain(Operation<?> operation, Criteria.Operator operator, QueryMetadata metadata) {
        Criteria lCriteria = toCriteria(operation.getArg(0), metadata);
        Criteria rCriteria = toCriteria(operation.getArg(1), metadata);

        if (operator.equals(Criteria.Operator.OR)) {
            return lCriteria.or(rCriteria);
        }
        return lCriteria.and(rCriteria);
    }

    /**
     * Converts a Querydsl expression and query metadata into an Elasticsearch {@link Criteria}.
     *
     * @param expression The Querydsl expression to be converted.
     * @param metadata   The query metadata associated with the expression.
     * @return Returns the Elasticsearch {@link Criteria} representing the Querydsl expression.
     */
    public Criteria toCriteria(Expression<?> expression, QueryMetadata metadata) {
        if (expression instanceof Operation<?>) {
            return toCriteria((Operation<?>) expression, metadata);
        } else {
            return toCriteria(ExpressionUtils.extract(expression), metadata);
        }
    }

    /**
     * Converts a list of Querydsl order specifiers into an {@link Sort} object.
     *
     * @param orderBys The list of Querydsl order specifiers to be converted.
     * @return Returns the {@link Sort} object representing the Querydsl order specifiers.
     */
    public Sort toSort(List<? extends OrderSpecifier<?>> orderBys) {
        return new SortSerializer().accept(orderBys, null);
    }
}
