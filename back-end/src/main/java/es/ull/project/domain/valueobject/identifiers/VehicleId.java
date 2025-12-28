package es.ull.project.domain.valueobject.identifiers;


import java.util.Objects;
import java.util.UUID;

/**
 * VehicleId
 * Represents the unique identifier of a vehicle.
 * Defined only by its value (UUID).
 * No behavior apart from equality.
 */
public final class VehicleId {

    private static final String ERROR_ID_NOT_DEFINED = "VehicleId is not defined";

    /**
     * UUID value of the vehicle identifier.
     */
    private final UUID value;

    /**
     * Creates a new VehicleId with a given UUID.
     * 
     * @param value UUID value of the vehicle identifier.
     */
    public VehicleId(UUID value) {
        this.validateValue(value);
        this.value = value;
    }

    /**
     * Validates the UUID value.
     * 
     * @param value UUID to validate.
     */
    private void validateValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_ID_NOT_DEFINED);
        }
    }

    /**
     * Returns the UUID value of the vehicle identifier.
     * 
     * @return UUID value.
     */
    public UUID getValue() {
        return this.value;
    }

    /**
     * Creates a new VehicleId with a randomly generated UUID.
     * 
     * @return New VehicleId instance.
     */
    public static VehicleId random() {
        return new VehicleId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        VehicleId otherId = (VehicleId) otherObject;
        return value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("VehicleId={value=%s}", this.value);
    }
}