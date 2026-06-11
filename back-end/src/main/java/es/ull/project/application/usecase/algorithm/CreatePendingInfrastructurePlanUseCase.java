package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.algorithm.AveragePickupTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.AverageTransferTimeMinutes;
import es.ull.project.domain.valueobject.algorithm.CollectionStartTime;
import es.ull.project.domain.valueobject.algorithm.GreedyWeights;
import es.ull.project.domain.valueobject.algorithm.NumberOfDays;
import es.ull.project.domain.valueobject.cost.MaximumBudget;

/**
 * Use case for persisting a placeholder infrastructure plan before asynchronous algorithm execution.
 */
public interface CreatePendingInfrastructurePlanUseCase {

    /**
     * Creates and stores an infrastructure plan in {@code RUNNING} state with request metadata only.
     *
     * @param numberOfDays               planning horizon from the client request
     * @param averagePickupTimeMinutes     average pickup time from the client request
     * @param collectionStartTime        collection journey start time from the client request
     * @param averageTransferTimeMinutes average transfer time from the client request
     * @param greedyWeights              greedy scoring weights from the client request
     * @param maxBudget                    maximum budget from the client request (required)
     * @param executionRequestJson         JSON snapshot of the client execution request
     * @return the persisted placeholder plan
     */
    InfrastructurePlan createPending(
            NumberOfDays numberOfDays,
            AveragePickupTimeMinutes averagePickupTimeMinutes,
            CollectionStartTime collectionStartTime,
            AverageTransferTimeMinutes averageTransferTimeMinutes,
            GreedyWeights greedyWeights,
            MaximumBudget maxBudget,
            AlgorithmJsonPayload executionRequestJson);
}
