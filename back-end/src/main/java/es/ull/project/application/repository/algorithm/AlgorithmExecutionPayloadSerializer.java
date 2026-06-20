package es.ull.project.application.repository.algorithm;

import es.ull.project.application.usecase.algorithm.AlgorithmExecutionResult;
import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;
import es.ull.project.domain.valueobject.cost.MaximumBudget;

/**
 * Serializes a processed algorithm execution result into JSON for the external runner.
 */
public interface AlgorithmExecutionPayloadSerializer {

    /**
     * Builds the JSON payload passed to the algorithm container.
     *
     * @param result    resolved entities and planning parameters
     * @param maxBudget maximum budget applied to the execution
     * @return JSON payload for the algorithm runner
     */
    AlgorithmJsonPayload serialize(AlgorithmExecutionResult result, MaximumBudget maxBudget);
}
