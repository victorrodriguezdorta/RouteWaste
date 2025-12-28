package es.ull.project.domain.valueobject.clasification;


import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public final class FacilityType {

    // Allowed facility types
    private static final Set<String> ALLOWED_TYPES = new HashSet<>() {{
        add("BaseOperativa");
        add("EstacionTransferencia");
        add("PlantaTratamiento");
    }};

    private static final String ERROR_TYPE_NOT_DEFINED = "Facility type is not defined";
    private static final String ERROR_TYPE_INVALID = "Facility type is invalid. Allowed types: " + ALLOWED_TYPES;

    /**
     * Type of the facility.
     * Required attribute.
     */
    private final String type;

    /**
     * It creates a new FacilityType value object.
     *
     * @param type Type of the facility.
     */
    public FacilityType(String type) {
        validateType(type);
        this.type = type;
    }

    /**
     * Validates the type of the facility.
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
     * Returns the facility type.
     *
     * @return Type of the facility.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns a new FacilityType with the new type.
     *
     * @param newType New type of the facility.
     * @return New FacilityType object.
     */
    public FacilityType setType(String newType) {
        return new FacilityType(newType);
    }

    /**
     * Compares this FacilityType with another object.
     *
     * @param otherObject Object to compare.
     * @return True if both FacilityType objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        FacilityType other = (FacilityType) otherObject;
        return this.type.equals(other.type);
    }

    /**
     * Returns the hash code of the FacilityType.
     *
     * @return Hash code of the facility type.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    /**
     * Returns the string representation of the FacilityType.
     *
     * @return String representation of the facility type.
     */
    @Override
    public String toString() {
        return String.format("FacilityType={type='%s'}", this.type);
    }
}