package es.ull.project.application.usecase.algorithm;

/**
 * Starts algorithm execution jobs without blocking the caller thread.
 */
public interface RunAlgorithmExecutionJobAsyncUseCase {

    /**
     * Schedules asynchronous execution of the algorithm pipeline.
     *
     * @param command job input including the pending plan identifier
     */
    void runAsync(AlgorithmExecutionJobCommand command);
}
