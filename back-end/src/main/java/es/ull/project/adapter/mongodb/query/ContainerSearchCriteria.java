package es.ull.project.adapter.mongodb.query;

import es.ull.project.domain.enumerate.WasteType;

/**
 * Encapsulates search criteria for Container queries.
 * Supports dynamic filtering by multiple attributes.
 *
 * This class allows building flexible queries without changing the repository
 * or controller code. Criteria can be easily added or removed.
 * Instances are created via {@link ContainerSearchCriteriaBuilder}.
 */
public class ContainerSearchCriteria {

    private WasteType wasteType;
    private String serviceZone;
    private Integer minCapacityLiters;
    private Integer maxCapacityLiters;
    private Integer minDailyDemand;
    private Integer maxDailyDemand;
    private String locationPostalAddress;

    /**
     * Private constructor enforcing use of {@link ContainerSearchCriteriaBuilder}.
     */
    ContainerSearchCriteria() {}

    /**
     * Returns the waste type filter criterion.
     *
     * @return the {@link WasteType} to filter by, or {@code null} if not set
     */
    public WasteType getWasteType() {
        return wasteType;
    }

    /**
     * Returns the service zone filter criterion.
     *
     * @return the service zone string to filter by, or {@code null} if not set
     */
    public String getServiceZone() {
        return serviceZone;
    }

    /**
     * Returns the minimum capacity filter criterion in liters.
     *
     * @return the minimum capacity in liters, or {@code null} if not set
     */
    public Integer getMinCapacityLiters() {
        return minCapacityLiters;
    }

    /**
     * Returns the maximum capacity filter criterion in liters.
     *
     * @return the maximum capacity in liters, or {@code null} if not set
     */
    public Integer getMaxCapacityLiters() {
        return maxCapacityLiters;
    }

    /**
     * Returns the minimum daily demand filter criterion.
     *
     * @return the minimum daily demand, or {@code null} if not set
     */
    public Integer getMinDailyDemand() {
        return minDailyDemand;
    }

    /**
     * Returns the maximum daily demand filter criterion.
     *
     * @return the maximum daily demand, or {@code null} if not set
     */
    public Integer getMaxDailyDemand() {
        return maxDailyDemand;
    }

    /**
     * Returns the location postal address filter criterion.
     *
     * @return the postal address substring to filter by, or {@code null} if not set
     */
    public String getLocationPostalAddress() {
        return locationPostalAddress;
    }

    /**
     * Checks whether any search criterion has been set.
     *
     * @return {@code true} if at least one criterion is non-null, {@code false} otherwise
     */
    public boolean hasCriteria() {
        return wasteType != null
            || serviceZone != null
            || minCapacityLiters != null
            || maxCapacityLiters != null
            || minDailyDemand != null
            || maxDailyDemand != null
            || locationPostalAddress != null;
    }

    /**
     * Sets the waste type criterion (package-private, called by builder).
     *
     * @param wasteType the waste type to filter by
     */
    void setWasteType(WasteType wasteType) {
        this.wasteType = wasteType;
    }

    /**
     * Sets the service zone criterion (package-private, called by builder).
     *
     * @param serviceZone the service zone to filter by
     */
    void setServiceZone(String serviceZone) {
        this.serviceZone = serviceZone;
    }

    /**
     * Sets the minimum capacity criterion in liters (package-private, called by builder).
     *
     * @param minCapacityLiters the minimum capacity in liters
     */
    void setMinCapacityLiters(Integer minCapacityLiters) {
        this.minCapacityLiters = minCapacityLiters;
    }

    /**
     * Sets the maximum capacity criterion in liters (package-private, called by builder).
     *
     * @param maxCapacityLiters the maximum capacity in liters
     */
    void setMaxCapacityLiters(Integer maxCapacityLiters) {
        this.maxCapacityLiters = maxCapacityLiters;
    }

    /**
     * Sets the minimum daily demand criterion (package-private, called by builder).
     *
     * @param minDailyDemand the minimum daily demand
     */
    void setMinDailyDemand(Integer minDailyDemand) {
        this.minDailyDemand = minDailyDemand;
    }

    /**
     * Sets the maximum daily demand criterion (package-private, called by builder).
     *
     * @param maxDailyDemand the maximum daily demand
     */
    void setMaxDailyDemand(Integer maxDailyDemand) {
        this.maxDailyDemand = maxDailyDemand;
    }

    /**
     * Sets the location postal address criterion (package-private, called by builder).
     *
     * @param locationPostalAddress the postal address substring to filter by
     */
    void setLocationPostalAddress(String locationPostalAddress) {
        this.locationPostalAddress = locationPostalAddress;
    }
}
