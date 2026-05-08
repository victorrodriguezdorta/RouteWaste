package es.ull.project.adapter.rest.response.algorithm;

import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import java.util.List;

/**
 * AlgorithmExecutionResponseBody
 *
 * Represents the processed response returned by the algorithm execution
 * endpoint after resolving all requested identifiers.
 */
public class AlgorithmExecutionResponseBody {

    /**
     * Facilities and vehicles resolved from the incoming request.
     */
    public List<AlgorithmFacilityVehiclesResponseBody> facilitiesWithVehicles;

    /**
     * Containers resolved from the incoming request.
     */
    public List<ContainerResponseBody> selectedContainers;

    /**
     * Number of planning days received in the request.
     */
    public int numberOfDays;

    /**
     * Average pickup time received in the request.
     */
    public int averagePickupTimeMinutes;

    /**
     * Optional maximum budget included in the processed payload.
     * This field is populated only when the client provides a budget constraint.
     */
    public MaximumBudgetResponseBody maxBudget;
}
