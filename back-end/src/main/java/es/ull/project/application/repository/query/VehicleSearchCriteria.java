package es.ull.project.application.repository.query;

import es.ull.project.domain.enumerate.VehicleType;

/**
 * Encapsulates search criteria for Vehicle queries.
 * Supports dynamic filtering by multiple attributes.
 */
public class VehicleSearchCriteria {

    private VehicleType vehicleType;
    private String name;

    /**
     * Public constructor for use by builders.
     */
    public VehicleSearchCriteria() {}

    /**
     * Returns the vehicle type filter criterion.
     *
     * @return the {@link VehicleType} to filter by, or {@code null} if not set
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * Returns the name filter criterion.
     *
     * @return the name substring to filter by, or {@code null} if not set
     */
    public String getName() {
        return name;
    }

    /**
     * Checks whether any search criterion has been set.
     *
     * @return {@code true} if at least one criterion is non-null, {@code false} otherwise
     */
    public boolean hasCriteria() {
        return vehicleType != null || name != null;
    }

    /**
     * Sets the vehicle type criterion.
     *
     * @param vehicleType the vehicle type to filter by
     */
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Sets the name criterion.
     *
     * @param name the name substring to filter by
     */
    public void setName(String name) {
        this.name = name;
    }
}
