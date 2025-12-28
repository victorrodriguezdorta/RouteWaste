package es.ull.project.domain.valueobject.clasification;


import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public final class ServiceZone {

    // Allowed service zones
    private static final Set<String> ALLOWED_ZONES = new HashSet<>() {{
        add("Barrio");
        add("Distrito");
        add("AreaGeografica");
    }};

    private static final String ERROR_ZONE_NOT_DEFINED = "Service zone is not defined";
    private static final String ERROR_ZONE_INVALID = "Service zone is invalid. Allowed zones: " + ALLOWED_ZONES;

    /**
     * Zone of service.
     * Required attribute.
     */
    private final String zone;

    /**
     * Creates a new ServiceZone value object.
     *
     * @param zone Zone of service.
     */
    public ServiceZone(String zone) {
        validateZone(zone);
        this.zone = zone;
    }

    /**
     * Validates the service zone.
     *
     * @param zone Zone to validate.
     */
    private void validateZone(String zone) {
        if (zone == null) {
            throw new IllegalArgumentException(ERROR_ZONE_NOT_DEFINED);
        }
        if (!ALLOWED_ZONES.contains(zone)) {
            throw new IllegalArgumentException(ERROR_ZONE_INVALID);
        }
    }

    /**
     * Returns the service zone.
     *
     * @return Zone of service.
     */
    public String getZone() {
        return this.zone;
    }

    /**
     * Returns a new ServiceZone with the new zone.
     *
     * @param newZone New zone of service.
     * @return New ServiceZone object.
     */
    public ServiceZone setZone(String newZone) {
        return new ServiceZone(newZone);
    }

    /**
     * Compares this ServiceZone with another object.
     *
     * @param otherObject Object to compare.
     * @return True if both ServiceZone objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        ServiceZone other = (ServiceZone) otherObject;
        return this.zone.equals(other.zone);
    }

    /**
     * Returns the hash code of the ServiceZone.
     *
     * @return Hash code of the service zone.
     */
    @Override
    public int hashCode() {
        return Objects.hash(zone);
    }

    /**
     * Returns the string representation of the ServiceZone.
     *
     * @return String representation of the service zone.
     */
    @Override
    public String toString() {
        return String.format("ServiceZone={zone='%s'}", this.zone);
    }
}