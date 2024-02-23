package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;

/**
 * Prepare a {@link Criteria} and extract values to perform a mathematical operation.
 * This can be used for operations like the {@link Ops#LT} operator.
 */
class MathSerializer extends OperationSerializer {
    private final BiConsumer<Criteria, List<String>> criteriaConsumer;

    public MathSerializer(BiConsumer<Criteria, List<String>> criteriaConsumer) {
        this.criteriaConsumer = criteriaConsumer;
    }

    @NonNull
    @Override
    public Criteria accept(@NonNull Operation<?> input, QueryMetadata metadata) {
        Path<?> path = getPath(input.getArg(0));
        List<String> values = new ArrayList<>();
        for (int idx = 1; idx < input.getArgs().size(); idx++) {
            values.addAll(asList(getValues(input.getArg(idx), metadata)));
        }

        Criteria criteria = Criteria.where(toField(path));
        criteriaConsumer.accept(criteria, values);
        return criteria;
    }
}
