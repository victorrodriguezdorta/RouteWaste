package es.ull.project.adapter.rest.response.algorithm;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.adapter.rest.serialization.algorithm.AlgorithmExecutionResponseBodySerializer;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import java.util.List;

/**
 * AlgorithmExecutionResponseBody
 *
 * Represents the processed response returned by the algorithm execution
 * endpoint after resolving all requested identifiers.
 */
@JsonSerialize(using = AlgorithmExecutionResponseBodySerializer.class)
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
    public NumberOfDays numberOfDays;

    /**
     * Average pickup time received in the request.
     */
    public AveragePickupTimeMinutes averagePickupTimeMinutes;

    /**
     * Optional maximum budget included in the processed payload.
     * This field is populated only when the client provides a budget constraint.
     */
    public MaximumBudget maxBudget;
}
