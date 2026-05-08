package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;

/**
 * Use case for persisting the algorithm execution result.
 */
public interface PersistAlgorithmExecutionResultUseCase {

    /**
     * Persists the raw algorithm response as an infrastructure plan aggregate.
     *
     * @param algorithmResponse JSON returned by the algorithm runner
     * @param numberOfDays number of days for the planning period
     * @param averagePickupTimeMinutes average pickup time in minutes
     * @param providedMaxBudget optional maximum budget provided in the request
     * @return the persisted infrastructure plan
     */
    InfrastructurePlan persist(
            AlgorithmJsonPayload algorithmResponse,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            MaximumBudget providedMaxBudget);
}