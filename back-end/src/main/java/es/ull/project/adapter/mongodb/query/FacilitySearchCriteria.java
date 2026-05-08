package es.ull.project.adapter.mongodb.query;

import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;

/**
 * Encapsulates search criteria for Facility queries.
 * Supports dynamic filtering by multiple attributes.
 *
 * This class allows building flexible queries without changing the repository
 * or controller code. Criteria can be easily added or removed.
 * Instances are created via {@link FacilitySearchCriteriaBuilder}.
 */
public class FacilitySearchCriteria {

    private FacilityType facilityType;
    private FacilityStatus status;
    private String locationPostalAddress;

    /**
     * Package-private constructor enforcing use of {@link FacilitySearchCriteriaBuilder}.
     */
    FacilitySearchCriteria() {}

    /**
     * Returns the facility type filter criterion.
     *
     * @return the {@link FacilityType} to filter by, or {@code null} if not set
     */
    public FacilityType getFacilityType() {
        return facilityType;
    }

    /**
     * Returns the facility status filter criterion.
     *
     * @return the {@link FacilityStatus} to filter by, or {@code null} if not set
     */
    public FacilityStatus getStatus() {
        return status;
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
        return facilityType != null
            || status != null
            || locationPostalAddress != null;
    }

    /**
     * Sets the facility type criterion (package-private, called by builder).
     *
     * @param facilityType the facility type to filter by
     */
    void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    /**
     * Sets the facility status criterion (package-private, called by builder).
     *
     * @param status the facility status to filter by
     */
    void setStatus(FacilityStatus status) {
        this.status = status;
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
