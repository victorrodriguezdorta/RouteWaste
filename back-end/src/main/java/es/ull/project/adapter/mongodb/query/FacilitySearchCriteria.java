package es.ull.project.adapter.mongodb.query;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;

/**
 * Encapsulates search criteria for Facility queries.
 * Supports dynamic filtering by multiple attributes.
 * 
 * This class allows building flexible queries without changing the repository
 * or controller code. Criteria can be easily added or removed.
 */
public class FacilitySearchCriteria {

    private FacilityType facilityType;
    private FacilityStatus status;
    private String locationPostalAddress;

    // Private constructor for builder pattern
    private FacilitySearchCriteria() {}

    // Getters
    public FacilityType getFacilityType() {
        return facilityType;
    }

    public FacilityStatus getStatus() {
        return status;
    }

    public String getLocationPostalAddress() {
        return locationPostalAddress;
    }

    /**
     * Check if any criteria is set.
     */
    public boolean hasCriteria() {
        return facilityType != null 
            || status != null
            || locationPostalAddress != null;
    }

    /**
     * Builder pattern for constructing search criteria.
     */
    public static class Builder {
        private FacilityType facilityType;
        private FacilityStatus status;
        private String locationPostalAddress;

        public Builder withFacilityType(FacilityType facilityType) {
            this.facilityType = facilityType;
            return this;
        }

        public Builder withStatus(FacilityStatus status) {
            this.status = status;
            return this;
        }

        public Builder withLocationPostalAddress(String postalAddress) {
            this.locationPostalAddress = postalAddress == null || postalAddress.isBlank() ? null : postalAddress;
            return this;
        }

        public FacilitySearchCriteria build() {
            FacilitySearchCriteria criteria = new FacilitySearchCriteria();
            criteria.facilityType = this.facilityType;
            criteria.status = this.status;
            criteria.locationPostalAddress = this.locationPostalAddress;
            return criteria;
        }
    }
}
