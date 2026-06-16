package es.ull.project.adapter.rest.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Base deserializer for bulk POST payloads.
 * Accepts either a top-level JSON array or an object wrapping the array.
 *
 * @param <T> item request body type
 * @param <B> bulk wrapper request body type
 */
public abstract class AbstractBulkPostRequestBodyDeserializer<T, B> extends JsonDeserializer<B> {

    private static final String FIELD_EMPTY_BATCH = "batch";
    private static final String MSG_EMPTY_BATCH = "At least one item is required";
    private static final String MSG_INVALID_ROOT = "Expected a JSON array or an object with field '";
    private static final String MSG_INVALID_ARRAY = "Must be a non-null JSON array";

    private final JsonDeserializer<T> itemDeserializer;
    private final Supplier<B> wrapperSupplier;
    private final String wrapperFieldName;
    private final BiConsumer<B, List<T>> itemsSetter;

    /**
     * Configures the bulk deserializer with item parsing and wrapper wiring.
     *
     * @param itemDeserializer   deserializer for a single item
     * @param wrapperSupplier    creates an empty bulk wrapper
     * @param wrapperFieldName   property name when payload is an object (e.g. {@code containers})
     * @param itemsSetter        assigns parsed items to the wrapper
     */
    protected AbstractBulkPostRequestBodyDeserializer(
            JsonDeserializer<T> itemDeserializer,
            Supplier<B> wrapperSupplier,
            String wrapperFieldName,
            BiConsumer<B, List<T>> itemsSetter) {
        this.itemDeserializer = itemDeserializer;
        this.wrapperSupplier = wrapperSupplier;
        this.wrapperFieldName = wrapperFieldName;
        this.itemsSetter = itemsSetter;
    }

    /**
     * Deserializes a bulk POST payload from a JSON array or wrapped object.
     *
     * @param parser  the JSON parser
     * @param context the deserialization context
     * @return bulk wrapper with parsed items
     * @throws IOException if parsing fails
     */
    @Override
    public B deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        JsonNode arrayNode = resolveArrayNode(rootNode);
        if (arrayNode.isEmpty()) {
            throw new ValidationException(List.of(new FieldError(FIELD_EMPTY_BATCH, MSG_EMPTY_BATCH)));
        }
        List<FieldError> errors = new ArrayList<>();
        List<T> items = new ArrayList<>();
        for (int index = 0; index < arrayNode.size(); index++) {
            JsonNode itemNode = arrayNode.get(index);
            try {
                JsonParser itemParser = itemNode.traverse(parser.getCodec());
                itemParser.nextToken();
                T item = this.itemDeserializer.deserialize(itemParser, context);
                items.add(item);
            } catch (ValidationException validationException) {
                for (FieldError fieldError : validationException.getErrors()) {
                    errors.add(new FieldError("[" + index + "]." + fieldError.getField(), fieldError.getIssue()));
                }
            } catch (JsonMappingException mappingException) {
                errors.add(new FieldError("[" + index + "]", mappingException.getOriginalMessage()));
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        B wrapper = this.wrapperSupplier.get();
        this.itemsSetter.accept(wrapper, items);
        return wrapper;
    }

    /**
     * Resolves the JSON array node from either a root array or a wrapper object.
     *
     * @param rootNode parsed JSON root
     * @return array node containing bulk items
     */
    private JsonNode resolveArrayNode(JsonNode rootNode) {
        if (rootNode.isArray()) {
            return rootNode;
        }
        if (rootNode.isObject() && rootNode.has(this.wrapperFieldName)) {
            JsonNode arrayNode = rootNode.get(this.wrapperFieldName);
            if (arrayNode == null || !arrayNode.isArray()) {
                throw new ValidationException(List.of(
                        new FieldError(this.wrapperFieldName, MSG_INVALID_ARRAY)));
            }
            return arrayNode;
        }
        throw new ValidationException(List.of(
                new FieldError(this.wrapperFieldName, MSG_INVALID_ROOT + this.wrapperFieldName + "'")));
    }
}
