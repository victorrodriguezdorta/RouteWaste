package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.entity.Container;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import java.util.List;

/**
 * AlgorithmExecutionResult
 *
 * Contains the processed data resolved from the algorithm execution request.
 * For now, this object is used to return the full entities recovered from the
 * database so the frontend can validate the request flow.
 */
public class AlgorithmExecutionResult {

    /**
     * Facility selections enriched with full facility and vehicle data.
     */
    private final List<ResolvedFacilityVehiclesSelection> facilitiesWithVehicles;

    /**
     * Selected containers enriched with full container data.
     */
    private final List<Container> selectedContainers;

    /**
     * Number of days received in the request.
     */
    private final NumberOfDays numberOfDays;

    /**
     * Average pickup time received in the request.
     */
    private final AveragePickupTimeMinutes averagePickupTimeMinutes;

    /**
     * Time of day when the collection journey starts.
     */
    private final CollectionStartTime collectionStartTime;

    /**
     * Average travelling time between points received in the request.
     */
    private final AverageTransferTimeMinutes averageTransferTimeMinutes;

    /**
     * Weights applied to the greedy selection score received in the request.
     */
    private final GreedyWeights greedyWeights;

    /**
     * Creates a processed algorithm execution result.
     *
     * @param facilitiesWithVehicles resolved facilities and vehicles
     * @param selectedContainers resolved containers
     * @param numberOfDays number of planning days
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @param collectionStartTime time of day when the collection journey starts
     * @param averageTransferTimeMinutes average travelling time between points in minutes
     * @param greedyWeights weights applied to the greedy selection score
     */
    public AlgorithmExecutionResult(
            List<ResolvedFacilityVehiclesSelection> facilitiesWithVehicles,
            List<Container> selectedContainers,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            CollectionStartTime collectionStartTime,
            AverageTransferTimeMinutes averageTransferTimeMinutes,
            GreedyWeights greedyWeights) {
        this.facilitiesWithVehicles = List.copyOf(facilitiesWithVehicles);
        this.selectedContainers = List.copyOf(selectedContainers);
        this.numberOfDays = numberOfDays;
        this.averagePickupTimeMinutes = averagePickupTimeMinutes;
        this.collectionStartTime = collectionStartTime;
        this.averageTransferTimeMinutes = averageTransferTimeMinutes;
        this.greedyWeights = greedyWeights;
    }

    /**
     * Returns the resolved facilities and vehicles.
     *
     * @return immutable list of resolved selections
     */
    public List<ResolvedFacilityVehiclesSelection> getFacilitiesWithVehicles() {
        return this.facilitiesWithVehicles;
    }

    /**
     * Returns the resolved containers.
     *
     * @return immutable list of containers
     */
    public List<Container> getSelectedContainers() {
        return this.selectedContainers;
    }

    /**
     * Returns the number of planning days.
     *
     * @return number of days
     */
    public NumberOfDays getNumberOfDays() {
        return this.numberOfDays;
    }

    /**
     * Returns the average pickup time in minutes.
     *
     * @return pickup time in minutes
     */
    public AveragePickupTimeMinutes getAveragePickupTimeMinutes() {
        return this.averagePickupTimeMinutes;
    }

    /**
     * Returns the time of day when the collection journey starts.
     *
     * @return collection start time
     */
    public CollectionStartTime getCollectionStartTime() {
        return this.collectionStartTime;
    }

    /**
     * Returns the average travelling time between points in minutes.
     *
     * @return average transfer time in minutes
     */
    public AverageTransferTimeMinutes getAverageTransferTimeMinutes() {
        return this.averageTransferTimeMinutes;
    }

    /**
     * Returns the weights applied to the greedy selection score.
     *
     * @return greedy scoring weights
     */
    public GreedyWeights getGreedyWeights() {
        return this.greedyWeights;
    }
}
