package es.ull.project.adapter.mongodb.query;

import es.ull.project.domain.enumerate.WasteType;

/**
 * Encapsulates search criteria for Container queries.
 * Supports dynamic filtering by multiple attributes.
 * 
 * This class allows building flexible queries without changing the repository
 * or controller code. Criteria can be easily added or removed.
 */
public class ContainerSearchCriteria {

    private WasteType wasteType;
    private String serviceZone;
    private Integer minCapacityLiters;
    private Integer maxCapacityLiters;
    private Integer minDailyDemand;
    private Integer maxDailyDemand;
    private String locationPostalAddress;

    // Private constructor for builder pattern
    private ContainerSearchCriteria() {}

    // Getters
    public WasteType getWasteType() {
        return wasteType;
    }

    public String getServiceZone() {
        return serviceZone;
    }

    public Integer getMinCapacityLiters() {
        return minCapacityLiters;
    }

    public Integer getMaxCapacityLiters() {
        return maxCapacityLiters;
    }

    public Integer getMinDailyDemand() {
        return minDailyDemand;
    }

    public Integer getMaxDailyDemand() {
        return maxDailyDemand;
    }

    public String getLocationPostalAddress() {
        return locationPostalAddress;
    }

    /**
     * Check if any criteria is set.
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
     * Builder pattern for constructing search criteria.
     */
    public static class Builder {
        private WasteType wasteType;
        private String serviceZone;
        private Integer minCapacityLiters;
        private Integer maxCapacityLiters;
        private Integer minDailyDemand;
        private Integer maxDailyDemand;
        private String locationPostalAddress;

        public Builder withWasteType(WasteType wasteType) {
            this.wasteType = wasteType;
            return this;
        }

        public Builder withServiceZone(String serviceZone) {
            this.serviceZone = serviceZone == null || serviceZone.isBlank() ? null : serviceZone;
            return this;
        }

        public Builder withMinCapacityLiters(Integer minCapacity) {
            this.minCapacityLiters = minCapacity;
            return this;
        }

        public Builder withMaxCapacityLiters(Integer maxCapacity) {
            this.maxCapacityLiters = maxCapacity;
            return this;
        }

        public Builder withMinDailyDemand(Integer minDemand) {
            this.minDailyDemand = minDemand;
            return this;
        }

        public Builder withMaxDailyDemand(Integer maxDemand) {
            this.maxDailyDemand = maxDemand;
            return this;
        }

        public Builder withLocationPostalAddress(String postalAddress) {
            this.locationPostalAddress = postalAddress == null || postalAddress.isBlank() ? null : postalAddress;
            return this;
        }

        public ContainerSearchCriteria build() {
            ContainerSearchCriteria criteria = new ContainerSearchCriteria();
            criteria.wasteType = this.wasteType;
            criteria.serviceZone = this.serviceZone;
            criteria.minCapacityLiters = this.minCapacityLiters;
            criteria.maxCapacityLiters = this.maxCapacityLiters;
            criteria.minDailyDemand = this.minDailyDemand;
            criteria.maxDailyDemand = this.maxDailyDemand;
            criteria.locationPostalAddress = this.locationPostalAddress;
            return criteria;
        }
    }
}
