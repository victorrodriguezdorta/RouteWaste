package es.ull.project.adapter.rest.request.algorithm;

import java.util.List;

/**
 * AlgorithmExecutionRequestBody
 *
 * Data Transfer Object representing the request body sent by the frontend when
 * it wants to execute the algorithm flow.
 */
public class AlgorithmExecutionRequestBody {

    /**
     * Facilities with the vehicles selected for each facility.
     */
    public List<FacilityVehiclesSelectionRequestBody> facilitiesWithVehicles;

    /**
     * Selected container identifiers.
     */
    public List<String> selectedContainerIds;

    /**
     * Number of planning days.
     */
    public Integer numberOfDays;

    /**
     * Average pickup time in minutes.
     */
    public Integer averagePickupTimeMinutes;
}
