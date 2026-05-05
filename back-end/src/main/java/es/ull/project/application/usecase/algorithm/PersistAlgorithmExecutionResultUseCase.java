package es.ull.project.application.usecase.algorithm;

import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.domain.entity.InfrastructurePlan;

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
    InfrastructurePlan persist(JsonNode algorithmResponse, Integer numberOfDays, Integer averagePickupTimeMinutes, es.ull.project.domain.valueobject.cost.MaximumBudget providedMaxBudget);
}