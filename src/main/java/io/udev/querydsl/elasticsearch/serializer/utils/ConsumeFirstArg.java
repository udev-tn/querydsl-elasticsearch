package io.udev.querydsl.elasticsearch.serializer.utils;

import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * A utility class used to consume the first argument of a list of strings.
 */
public final class ConsumeFirstArg implements BiConsumer<Criteria, List<String>> {

    private final BiConsumer<Criteria, String> criteriaConsumer;

    private ConsumeFirstArg(BiConsumer<Criteria, String> criteriaConsumer) {
        this.criteriaConsumer = criteriaConsumer;
    }

    public static ConsumeFirstArg of(BiConsumer<Criteria, String> consumer) {
        return new ConsumeFirstArg(consumer);
    }

    /**
     * Accepts a {@link Criteria} and a list of values, and passes the first value to the `consumer`.
     *
     * @param criteria The {@link Criteria} to be consumed.
     * @param values   The list of values from which the first value will be passed to the `consumer`.
     */
    @Override
    public void accept(Criteria criteria, List<String> values) {
        criteriaConsumer.accept(criteria, values.get(0));
    }
}
