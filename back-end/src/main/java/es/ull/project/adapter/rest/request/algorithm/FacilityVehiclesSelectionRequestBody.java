package es.ull.project.adapter.rest.request.algorithm;

import java.util.List;

/**
 * FacilityVehiclesSelectionRequestBody
 *
 * Represents one facility and the selected vehicle identifiers associated with
 * it in the algorithm execution request.
 */
public class FacilityVehiclesSelectionRequestBody {

    /**
     * Facility identifier received from the frontend.
     */
    public String facilityId;

    /**
     * Vehicle identifiers associated with the facility.
     */
    public List<String> selectedVehicleIds;
}
