package es.ull.project.adapter.mongodb.query;

import es.ull.project.application.query.ContainerSearchCriteria;
import es.ull.project.domain.enumerate.WasteType;

/**
 * Builder for constructing {@link ContainerSearchCriteria} instances.
 *
 * Provides a fluent API for setting optional filter criteria
 * before calling {@link #build()} to produce the immutable criteria object.
 */
public class ContainerSearchCriteriaBuilder {

    private WasteType wasteType;
    private String serviceZone;
    private Integer minCapacityLiters;
    private Integer maxCapacityLiters;
    private Integer minDailyDemand;
    private Integer maxDailyDemand;
    private String locationPostalAddress;
    private String name;

    /**
     * Sets the waste type filter criterion.
     *
     * @param wasteType the {@link WasteType} to filter by
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withWasteType(WasteType wasteType) {
        this.wasteType = wasteType;
        return this;
    }

    /**
     * Sets the service zone filter criterion.
     * Blank or null values are treated as no filter.
     *
     * @param serviceZone the service zone string to filter by
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withServiceZone(String serviceZone) {
        this.serviceZone = serviceZone == null || serviceZone.isBlank() ? null : serviceZone;
        return this;
    }

    /**
     * Sets the minimum capacity filter criterion in liters.
     *
     * @param minCapacity the minimum container capacity in liters
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withMinCapacityLiters(Integer minCapacity) {
        this.minCapacityLiters = minCapacity;
        return this;
    }

    /**
     * Sets the maximum capacity filter criterion in liters.
     *
     * @param maxCapacity the maximum container capacity in liters
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withMaxCapacityLiters(Integer maxCapacity) {
        this.maxCapacityLiters = maxCapacity;
        return this;
    }

    /**
     * Sets the minimum daily demand filter criterion.
     *
     * @param minDemand the minimum daily demand value
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withMinDailyDemand(Integer minDemand) {
        this.minDailyDemand = minDemand;
        return this;
    }

    /**
     * Sets the maximum daily demand filter criterion.
     *
     * @param maxDemand the maximum daily demand value
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withMaxDailyDemand(Integer maxDemand) {
        this.maxDailyDemand = maxDemand;
        return this;
    }

    /**
     * Sets the location postal address filter criterion.
     * Blank or null values are treated as no filter.
     *
     * @param postalAddress the postal address substring to filter by
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withLocationPostalAddress(String postalAddress) {
        this.locationPostalAddress = postalAddress == null || postalAddress.isBlank() ? null : postalAddress;
        return this;
    }

    /**
     * Sets the name filter criterion.
     * Blank or null values are treated as no filter.
     *
     * @param name the name substring to filter by
     * @return this builder instance for chaining
     */
    public ContainerSearchCriteriaBuilder withName(String name) {
        this.name = name == null || name.isBlank() ? null : name;
        return this;
    }

    /**
     * Constructs a {@link ContainerSearchCriteria} from the configured criteria.
     *
     * @return a new {@link ContainerSearchCriteria} instance
     */
    public ContainerSearchCriteria build() {
        ContainerSearchCriteria criteria = new ContainerSearchCriteria();
        criteria.setWasteType(this.wasteType);
        criteria.setServiceZone(this.serviceZone);
        criteria.setMinCapacityLiters(this.minCapacityLiters);
        criteria.setMaxCapacityLiters(this.maxCapacityLiters);
        criteria.setMinDailyDemand(this.minDailyDemand);
        criteria.setMaxDailyDemand(this.maxDailyDemand);
        criteria.setLocationPostalAddress(this.locationPostalAddress);
        criteria.setName(this.name);
        return criteria;
    }
}
