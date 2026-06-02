package es.ull.project.application.usecase.algorithm;

/**
 * Runs the full algorithm pipeline for a pending infrastructure plan.
 */
public interface RunAlgorithmExecutionJobUseCase {

    /**
     * Executes the algorithm pipeline and completes or fails the pending plan.
     *
     * @param command job input including the pending plan identifier
     */
    void run(AlgorithmExecutionJobCommand command);
}
