package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.infrastructureplan.InfrastructurePlanFailureReason;
import java.util.UUID;

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
     * @param executionRequestJson JSON snapshot of the client request used to run the algorithm (may be null)
     * @return the persisted infrastructure plan
     */
    InfrastructurePlan persist(
            AlgorithmJsonPayload algorithmResponse,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            MaximumBudget providedMaxBudget,
            AlgorithmJsonPayload executionRequestJson);

    /**
     * Completes a placeholder plan created in {@code RUNNING} state with algorithm output.
     *
     * @param planId                     identifier of the pending infrastructure plan
     * @param algorithmResponse          JSON returned by the algorithm runner
     * @param numberOfDays               number of days for the planning period
     * @param averagePickupTimeMinutes   average pickup time in minutes
     * @param providedMaxBudget          optional maximum budget provided in the request
     * @param executionRequestJson       JSON snapshot of the client request (may be null)
     * @return the updated infrastructure plan in {@code COMPLETED} state
     */
    InfrastructurePlan complete(
            UUID planId,
            AlgorithmJsonPayload algorithmResponse,
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            MaximumBudget providedMaxBudget,
            AlgorithmJsonPayload executionRequestJson);

    /**
     * Marks a pending plan as failed when asynchronous execution cannot finish successfully.
     *
     * @param planId         identifier of the pending infrastructure plan
     * @param failureReason  optional human-readable failure description
     * @return the updated infrastructure plan in {@code FAILED} state
     */
    InfrastructurePlan markExecutionFailed(UUID planId, InfrastructurePlanFailureReason failureReason);
}