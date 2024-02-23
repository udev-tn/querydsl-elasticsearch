package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Specializes in converting a {@link List<OrderSpecifier>} of order specifiers into a {@link Sort} object for serialization purposes.
 */
public class SortSerializer extends AbstractSerializer<List<? extends OrderSpecifier<?>>, Sort> {

    @NonNull
    @Override
    public Sort accept(@NonNull final List<? extends OrderSpecifier<?>> value, QueryMetadata metadata) {
        List<Sort.Order> orders = new ArrayList<>(value.size());
        for (OrderSpecifier<?> order : value) {
            if (!(order.getTarget() instanceof Path<?>)) {
                throw new IllegalArgumentException("argument was not of type Path.");
            }

            boolean isAscending = order.isAscending();
            Path<?> path = getPath(order.getTarget());
            orders.add(new Sort.Order(isAscending ? Sort.Direction.ASC : Sort.Direction.DESC, toField(path)));
        }

        return Sort.by(orders);
    }
}
