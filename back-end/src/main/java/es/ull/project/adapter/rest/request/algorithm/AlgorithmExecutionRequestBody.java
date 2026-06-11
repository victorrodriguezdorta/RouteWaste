package es.ull.project.adapter.rest.request.algorithm;

import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import java.util.List;
import java.util.UUID;

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
    public List<UUID> selectedContainerIds;

    /**
     * Number of planning days.
     */
    public NumberOfDays numberOfDays;

    /**
     * Average pickup time in minutes.
     */
    public AveragePickupTimeMinutes averagePickupTimeMinutes;

    /**
     * Time of day when the collection journey starts.
     */
    public CollectionStartTime collectionStartTime;

    /**
     * Average travelling time in minutes between two points.
     */
    public AverageTransferTimeMinutes averageTransferTimeMinutes;

    /**
     * Weights applied to the greedy container selection score (distance vs. fill).
     */
    public GreedyWeights greedyWeights;

    /**
     * Optional maximum budget to be provided to the algorithm.
     */
    public MaximumBudget maxBudget;
}
