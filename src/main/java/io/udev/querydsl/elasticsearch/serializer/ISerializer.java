package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Interface represents a serializer that converts an object to the output type {@link O}.
 *
 * @param <O> The output type.
 */
@FunctionalInterface
public interface ISerializer<I, O> {
    /**
     * Executes the serialization process to get the output object.
     *
     * @return Returns the serialized object.
     */
    @NonNull
    O accept(@NonNull final I input, @Nullable QueryMetadata metadata);

    /**
     * Is it necessary to convert the string to lowercase?
     *
     * @return true if the conversion to lowercase is enabled, otherwise false.
     */
    default boolean isLowerCase() {
        return false;
    }

    /**
     * Do we need to split it into terms?
     *
     * @return true if splitting into terms is enabled, otherwise false.
     */
    default boolean isSplitTerms() {
        return true;
    }
}
