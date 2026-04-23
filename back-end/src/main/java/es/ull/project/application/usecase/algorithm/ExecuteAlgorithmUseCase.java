package es.ull.project.application.usecase.algorithm;

import java.util.List;
import java.util.UUID;

/**
 * Use case for executing the algorithm request flow.
 */
public interface ExecuteAlgorithmUseCase {

    /**
     * Resolves the identifiers received in the execution request and returns the
     * processed data loaded from the database.
     *
     * @param facilitiesWithVehicles selected facilities and vehicle identifiers
     * @param selectedContainerIds selected container identifiers
     * @param numberOfDays number of planning days
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @return processed result with full resolved data
     */
    AlgorithmExecutionResult execute(
            List<AlgorithmExecutionSelection> facilitiesWithVehicles,
            List<UUID> selectedContainerIds,
            int numberOfDays,
            int averagePickupTimeMinutes);
}
