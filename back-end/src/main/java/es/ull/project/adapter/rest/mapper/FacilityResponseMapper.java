package es.ull.project.adapter.rest.mapper;

import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.domain.entity.Facility;

/**
 * Mapper class to convert Facility domain entities to FacilityResponseBody DTOs
 * This class handles the transformation between the domain layer and the REST adapter layer
 */
public class FacilityResponseMapper {

    /**
     * Converts a Facility domain entity to a FacilityResponseBody DTO
     * Maps all the facility properties including nested objects (location, capacity, cost, and demand)
     *
     * @param facility The Facility domain entity to convert
     * @return FacilityResponseBody DTO ready to be serialized as JSON
     */
    public static FacilityResponseBody toResponseBody(Facility facility) {
        FacilityResponseBody responseBody = new FacilityResponseBody();
        
        // Map facility ID
        responseBody.id = facility.getId();
        
        // Map facility type (convert enum to string)
        responseBody.facilityType = facility.getFacilityType().name();
        
        // Map location
        responseBody.location = new FacilityResponseBody.LocationData();
        responseBody.location.latitude = facility.getLocation().getLatitude();
        responseBody.location.longitude = facility.getLocation().getLongitude();
        responseBody.location.postalAddress = facility.getLocation().getPostalAddress();
        responseBody.location.gisReference = facility.getLocation().getGISReference();
        
        // Map capacity
        responseBody.capacity = new FacilityResponseBody.CapacityData();
        responseBody.capacity.value = facility.getCapacity().getValue();
        responseBody.capacity.quantityUnit = facility.getCapacity().getQuantityUnit().getValue();
        responseBody.capacity.timeUnit = facility.getCapacity().getTimeUnit().name();
        
        // Map opening fixed cost
        responseBody.openingFixedCost = new FacilityResponseBody.OpeningFixedCostData();
        responseBody.openingFixedCost.amount = facility.getOpeningFixedCost().getAmount();
        
        // Map currency (optional field)
        if (facility.getOpeningFixedCost().getCurrency().isPresent()) {
            responseBody.openingFixedCost.currency = facility.getOpeningFixedCost().getCurrency().get().getCode();
        }
        
        // Map facility status (convert enum to string)
        responseBody.status = facility.getStatus().name();
        
        // Map assigned waste demand
        responseBody.assignedWasteDemand = new FacilityResponseBody.AssignedWasteDemandData();
        responseBody.assignedWasteDemand.value = facility.getAssignedWasteDemand().getValue();
        responseBody.assignedWasteDemand.quantityUnit = facility.getAssignedWasteDemand().getQuantityUnit().getValue();
        responseBody.assignedWasteDemand.timeUnit = facility.getAssignedWasteDemand().getTimeUnit().name();
        
        return responseBody;
    }
}
