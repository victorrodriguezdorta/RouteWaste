package es.ull.project.adapter.mongodb.query;

import es.ull.project.application.repository.query.FacilitySearchCriteria;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;

/**
 * Builder for constructing {@link FacilitySearchCriteria} instances.
 *
 * Provides a fluent API for setting optional filter criteria
 * before calling {@link #build()} to produce the criteria object.
 */
public class FacilitySearchCriteriaBuilder {

    private FacilityType facilityType;
    private FacilityStatus status;
    private String locationPostalAddress;
    private String name;

    /**
     * Sets the facility type filter criterion.
     *
     * @param facilityType the {@link FacilityType} to filter by
     * @return this builder instance for chaining
     */
    public FacilitySearchCriteriaBuilder withFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
        return this;
    }

    /**
     * Sets the facility status filter criterion.
     *
     * @param status the {@link FacilityStatus} to filter by
     * @return this builder instance for chaining
     */
    public FacilitySearchCriteriaBuilder withStatus(FacilityStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Sets the location postal address filter criterion.
     * Blank or null values are treated as no filter.
     *
     * @param postalAddress the postal address substring to filter by
     * @return this builder instance for chaining
     */
    public FacilitySearchCriteriaBuilder withLocationPostalAddress(String postalAddress) {
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
    public FacilitySearchCriteriaBuilder withName(String name) {
        this.name = name == null || name.isBlank() ? null : name;
        return this;
    }

    /**
     * Constructs a {@link FacilitySearchCriteria} from the configured criteria.
     *
     * @return a new {@link FacilitySearchCriteria} instance
     */
    public FacilitySearchCriteria build() {
        FacilitySearchCriteria criteria = new FacilitySearchCriteria();
        criteria.setFacilityType(this.facilityType);
        criteria.setStatus(this.status);
        criteria.setLocationPostalAddress(this.locationPostalAddress);
        criteria.setName(this.name);
        return criteria;
    }
}
