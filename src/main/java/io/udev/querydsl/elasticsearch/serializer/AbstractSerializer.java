package io.udev.querydsl.elasticsearch.serializer;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;
import org.springframework.data.elasticsearch.core.query.Field;
import org.springframework.data.elasticsearch.core.query.SimpleField;
import org.springframework.lang.NonNull;

/**
 * Provides common functionality for serializing objects of type {@link T} to objects of type {@link O}.
 *
 * @param <T> The type of the object to be serialized.
 * @param <O> The type of the serialized object.
 */
abstract class AbstractSerializer<T, O>
        implements ISerializer<T, O> {

    /**
     * Returns the path of the given expression.
     *
     * @param expression The expression for which the path needs to be retrieved.
     * @return Returns the path of the expression.
     */
    protected final Path<?> getPath(Expression<?> expression) {
        if (expression instanceof Path<?>) {
            return (Path<?>) expression;
        } else if (expression instanceof Operation<?>) {
            Operation<?> operation = (Operation<?>) expression;
            if (operation.getArgs().size() == 1) {
                return (Path<?>) operation.getArg(0);
            }
        }

        throw new IllegalArgumentException("Unable to transform " + expression + " to path");
    }

    /**
     * Returns the string values of the given object.
     *
     * @param value The object for which the string values need to be retrieved.
     * @return Returns an array of string values.
     */
    protected String[] getValues(@NonNull Object value) {
        String toString = value.toString();

        // Convert the string to lowercase if required.
        if (isLowerCase()) {
            toString = toString.toLowerCase();
        }

        // Split the string into terms if required.
        if (isSplitTerms()) {
            if (toString.isEmpty()) {
                return new String[]{toString};
            } else {
                return toString.split("\\s+");
            }
        } else {
            return new String[]{toString};
        }
    }

    /**
     * Returns the string values of the given expression.
     *
     * @param expression The expression for which the string values need to be retrieved.
     * @param metadata   The query metadata associated with the expression.
     * @return Returns an array of string values.
     */
    protected String[] getValues(Expression<?> expression, QueryMetadata metadata) {
        if (expression instanceof ParamExpression<?>) {
            Object value = metadata.getParams().get(expression);
            if (value == null) {
                throw new ParamNotSetException((ParamExpression<?>) expression);
            }

            return getValues(value);
        } else if (expression instanceof Constant<?>) {
            return getValues(((Constant<?>) expression).getConstant());
        }

        throw new IllegalArgumentException(expression.toString());
    }

    /**
     * Sanitizes the string values of the given expression.
     *
     * @param expression The expression for which the string values need to be sanitized.
     * @param metadata   The query metadata associated with the expression.
     * @return Returns an array of sanitized string values.
     */
    protected final String[] sanitizeValues(Expression<?> expression, QueryMetadata metadata) {
        String[] values = getValues(expression, metadata);
        for (int i = 0; i < values.length; i++) {
            values[i] = escape(values[i]);
        }

        return values;
    }

    /**
     * Extract field name from a provided path.
     *
     * @param path path to process.
     * @return field name from the provided path.
     */
    protected String toField(Path<?> path) {
        PathMetadata pMeta = path.getMetadata();
        if (pMeta.getPathType() == PathType.COLLECTION_ANY) {
            return toField(pMeta.getParent());
        } else {
            String name = pMeta.getName();
            if (pMeta.getParent() != null) {
                Path<?> parent = pMeta.getParent();
                if (parent.getMetadata().getPathType() != PathType.VARIABLE) {
                    name = toField(parent) + "." + name;
                }
            }
            return name;
        }
    }

    /**
     * Converts the given path to an Elasticsearch {@link Field}.
     *
     * @param path The path to be converted.
     * @return Returns the Elasticsearch {@link Field}.
     */
    protected Field toEsField(Path<?> path) {
        String fieldName = toField(path);
        Field field = new SimpleField(fieldName);
        if (fieldName.split("\\.").length > 1) {
            field.setPath(fieldName);
        }

        return field;
    }

    /**
     * Returns a String where those characters that TextParser expects to be escaped are escaped by a preceding
     * <code>\</code>. Copied from Apache 2 licensed {@link org.apache.lucene.queryparser.flexible.standard.QueryParserUtil}
     * class
     */
    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '['
                || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&'
                || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
