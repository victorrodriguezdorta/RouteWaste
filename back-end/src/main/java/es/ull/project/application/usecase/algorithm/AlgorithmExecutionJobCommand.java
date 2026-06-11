package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import java.util.List;
import java.util.UUID;

/**
 * Immutable input for asynchronous algorithm execution against an existing pending plan.
 */
public record AlgorithmExecutionJobCommand(
        UUID planId,
        List<AlgorithmExecutionSelection> facilitiesWithVehicles,
        List<UUID> selectedContainerIds,
        NumberOfDays numberOfDays,
        AveragePickupTimeMinutes averagePickupTimeMinutes,
        CollectionStartTime collectionStartTime,
        AverageTransferTimeMinutes averageTransferTimeMinutes,
        GreedyWeights greedyWeights,
        MaximumBudget maxBudget,
        AlgorithmJsonPayload executionRequestJson) {
}
