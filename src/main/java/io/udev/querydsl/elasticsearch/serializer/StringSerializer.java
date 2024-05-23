package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;

/**
 * Prepare a {@link Criteria} and extract values to perform a string operation.
 * This can be used for operations like the {@link Ops#LIKE} operator.
 */
class StringSerializer extends OperationSerializer {
    /**
     * A unary operator that can be used in manipulating values.
     */
    @Nullable
    private UnaryOperator<String> stringProcessor;

    /**
     * A consumer that can be used to perform a string operation.
     */
    private final BiConsumer<Criteria, List<String>> criteriaConsumer;

    public StringSerializer(BiConsumer<Criteria, List<String>> criteriaConsumer) {
        this.criteriaConsumer = criteriaConsumer;
    }

    public StringSerializer(BiConsumer<Criteria, List<String>> criteriaConsumer, @NonNull UnaryOperator<String> stringProcessor) {
        this.stringProcessor = stringProcessor;
        this.criteriaConsumer = criteriaConsumer;
    }

    @NonNull
    @Override
    public Criteria accept(@NonNull Operation<?> input, QueryMetadata metadata) {
        Path<?> path = getPath(input.getArg(0));
        String field = toField(path);
        String[] terms = {};
        if (input.getArgs().size() > 1) { // Detect the operation arguments if they exist.
            terms = sanitizeValues(input.getArg(1), metadata);
        }
        if (stringProcessor != null) {
            // Process terms
            for (int idx = 0; idx < terms.length; idx++) {
                terms[idx] = stringProcessor.apply(terms[idx]);
            }
        }

        Criteria criteria = Criteria.where(field);
        criteriaConsumer.accept(criteria, asList(terms));
        return criteria;
    }
}
