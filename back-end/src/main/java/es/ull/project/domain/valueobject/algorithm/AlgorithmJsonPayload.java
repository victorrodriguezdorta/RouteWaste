package es.ull.project.domain.valueobject.algorithm;

/**
 * AlgorithmJsonPayload
 *
 * Represents the raw JSON payload exchanged with the external algorithm process.
 * Wraps a plain String so that the application layer can reference a domain
 * value object instead of a raw {@code String} primitive.
 * It is a required attribute.
 */
public final class AlgorithmJsonPayload {

    private static final String ERROR_PAYLOAD_NULL = "Algorithm JSON payload must not be null or blank";

    /**
     * The raw JSON content.
     * It is a required attribute.
     */
    private final String json;

    /**
     * Creates a new AlgorithmJsonPayload.
     *
     * @param json the raw JSON string; must not be null or blank
     * @throws IllegalArgumentException if json is null or blank
     */
    public AlgorithmJsonPayload(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException(ERROR_PAYLOAD_NULL);
        }
        this.json = json;
    }

    /**
     * Returns the raw JSON content.
     *
     * @return the JSON string
     */
    public String getJson() {
        return json;
    }

    /**
     * Checks equality based on the JSON string value.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same JSON content
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        AlgorithmJsonPayload other = (AlgorithmJsonPayload) otherObject;
        return json.equals(other.json);
    }

    /**
     * Returns a hash code based on the JSON string.
     *
     * @return hash code for this instance
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(json);
    }

    /**
     * Returns a string representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "AlgorithmJsonPayload={json.length=" + json.length() + "}";
    }
}
