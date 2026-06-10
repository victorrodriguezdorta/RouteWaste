package es.ull.project.adapter.mongodb.query;

import es.ull.project.application.query.VehicleSearchCriteria;
import es.ull.project.domain.enumerate.VehicleType;

/**
 * Builder for constructing {@link VehicleSearchCriteria} instances.
 */
public class VehicleSearchCriteriaBuilder {

    private VehicleType vehicleType;
    private String name;

    /**
     * Sets the vehicle type filter criterion.
     *
     * @param vehicleType the {@link VehicleType} to filter by
     * @return this builder instance for chaining
     */
    public VehicleSearchCriteriaBuilder withVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    /**
     * Sets the name filter criterion.
     * Blank or null values are treated as no filter.
     *
     * @param name the name substring to filter by
     * @return this builder instance for chaining
     */
    public VehicleSearchCriteriaBuilder withName(String name) {
        this.name = name == null || name.isBlank() ? null : name;
        return this;
    }

    /**
     * Constructs a {@link VehicleSearchCriteria} from the configured criteria.
     *
     * @return a new {@link VehicleSearchCriteria} instance
     */
    public VehicleSearchCriteria build() {
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        criteria.setVehicleType(this.vehicleType);
        criteria.setName(this.name);
        return criteria;
    }
}
