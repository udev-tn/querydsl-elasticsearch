package io.udev.querydsl.elasticsearch.repository.utils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.OrderSpecifier.NullHandling;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for Querydsl.
 */
public final class QuerydslUtils {
    private QuerydslUtils() {
    }

    /**
     * Converts the given {@link org.springframework.data.domain.Sort.NullHandling} to the appropriate Querydsl
     * {@link NullHandling}.
     *
     * @param nullHandling must not be {@literal null}.
     */
    public static NullHandling toQueryDslNullHandling(@NonNull org.springframework.data.domain.Sort.NullHandling nullHandling) {
        return switch (nullHandling) {
            case NULLS_FIRST -> NullHandling.NullsFirst;
            case NULLS_LAST -> NullHandling.NullsLast;
            default -> NullHandling.Default;
        };
    }

    /**
     * Converts the given {@link Sort} to the appropriate Querydsl {@link OrderSpecifier}[].
     *
     * @param entityClass must not be {@literal null}.
     * @param sort        maybe {@literal null}.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> OrderSpecifier[] toQueryDslOrders(@NonNull Class<T> entityClass, @Nullable Sort sort) {
        if (sort == null) {
            sort = Sort.unsorted();
        }

        final List<OrderSpecifier<?>> specifiers = new ArrayList<>();
        for (Sort.Order order : sort) {
            specifiers.add(new OrderSpecifier(
                    order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                    buildOrderPropertyPathFrom(entityClass, order)
            ));
        }
        return specifiers.toArray(OrderSpecifier[]::new);
    }

    /**
     * Creates an {@link Expression} for the given {@link org.springframework.data.domain.Sort.Order} property.
     *
     * @param order must not be {@literal null}.
     */
    public static <T> Expression<?> buildOrderPropertyPathFrom(Class<T> aClass, @NonNull Sort.Order order) {
        PathBuilder<T> builder = new PathBuilder<>(aClass, "");
        PropertyPath path = PropertyPath.from(order.getProperty(), aClass);
        Expression<?> sortPropertyExpression = builder;

        while (path != null) {
            sortPropertyExpression = !path.hasNext() && order.isIgnoreCase() && String.class.equals(path.getType()) //
                    ? Expressions.stringPath((Path<?>) sortPropertyExpression, path.getSegment()).lower() //
                    : Expressions.path(path.getType(), (Path<?>) sortPropertyExpression, path.getSegment());

            path = path.next();
        }

        return sortPropertyExpression;
    }
}
