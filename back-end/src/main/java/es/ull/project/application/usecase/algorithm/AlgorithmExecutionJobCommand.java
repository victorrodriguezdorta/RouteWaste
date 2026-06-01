package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
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
        MaximumBudget maxBudget,
        AlgorithmJsonPayload executionRequestJson) {
}
