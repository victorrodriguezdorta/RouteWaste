package es.ull.project.application.usecase.algorithm;

import java.util.List;

import es.ull.project.domain.entity.Container;

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
    private final int numberOfDays;

    /**
     * Average pickup time received in the request.
     */
    private final int averagePickupTimeMinutes;

    /**
     * Creates a processed algorithm execution result.
     *
     * @param facilitiesWithVehicles resolved facilities and vehicles
     * @param selectedContainers resolved containers
     * @param numberOfDays number of planning days
     * @param averagePickupTimeMinutes average pickup time in minutes
     */
    public AlgorithmExecutionResult(
            List<ResolvedFacilityVehiclesSelection> facilitiesWithVehicles,
            List<Container> selectedContainers,
            int numberOfDays,
            int averagePickupTimeMinutes) {
        this.facilitiesWithVehicles = List.copyOf(facilitiesWithVehicles);
        this.selectedContainers = List.copyOf(selectedContainers);
        this.numberOfDays = numberOfDays;
        this.averagePickupTimeMinutes = averagePickupTimeMinutes;
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
    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    /**
     * Returns the average pickup time in minutes.
     *
     * @return pickup time in minutes
     */
    public int getAveragePickupTimeMinutes() {
        return this.averagePickupTimeMinutes;
    }
}
