package es.ull.project.domain.valueobject.clasification;


import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public final class WasteType {

    // Allowed waste types
    private static final Set<String> ALLOWED_TYPES = new HashSet<>() {{
        add("Organico");
        add("Envases");
        add("PapelCarton");
        add("Vidrio");
        add("Resto");
    }};

    private static final String ERROR_TYPE_NOT_DEFINED = "Waste type is not defined";
    private static final String ERROR_TYPE_INVALID = "Waste type is invalid. Allowed types: " + ALLOWED_TYPES;

    /**
     * Type of the waste.
     * Required attribute.
     */
    private final String type;

    /**
     * It creates a new WasteType value object.
     *
     * @param type Type of the waste.
     */
    public WasteType(String type) {
        validateType(type);
        this.type = type;
    }

    /**
     * Validates the waste type.
     *
     * @param type Type to validate.
     */
    private void validateType(String type) {
        if (type == null) {
            throw new IllegalArgumentException(ERROR_TYPE_NOT_DEFINED);
        }
        if (!ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(ERROR_TYPE_INVALID);
        }
    }

    /**
     * Returns the waste type.
     *
     * @return Type of the waste.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns a new WasteType with the new type.
     *
     * @param newType New type of the waste.
     * @return New WasteType object.
     */
    public WasteType setType(String newType) {
        return new WasteType(newType);
    }

    /**
     * Compares this WasteType with another object.
     *
     * @param otherObject Object to compare.
     * @return True if both WasteType objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        WasteType other = (WasteType) otherObject;
        return this.type.equals(other.type);
    }

    /**
     * Returns the hash code of the WasteType.
     *
     * @return Hash code of the waste type.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    /**
     * Returns the string representation of the WasteType.
     *
     * @return String representation of the waste type.
     */
    @Override
    public String toString() {
        return String.format("WasteType={type='%s'}", this.type);
    }
}