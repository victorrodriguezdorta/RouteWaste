package es.ull.project.configuration;

import es.ull.project.application.usecase.algorithm.AlgorithmExecutionJobCommand;
import es.ull.project.application.usecase.algorithm.RunAlgorithmExecutionJobAsyncUseCase;
import es.ull.project.application.usecase.algorithm.RunAlgorithmExecutionJobUseCase;
import org.springframework.scheduling.annotation.Async;

/**
 * Runs algorithm execution jobs asynchronously on a separate thread.
 */
public class AsyncRunAlgorithmExecutionJobRunner implements RunAlgorithmExecutionJobAsyncUseCase {

    private final RunAlgorithmExecutionJobUseCase runAlgorithmExecutionJobUseCase;

    /**
     * Creates the async runner.
     *
     * @param runAlgorithmExecutionJobUseCase synchronous job use case
     */
    public AsyncRunAlgorithmExecutionJobRunner(RunAlgorithmExecutionJobUseCase runAlgorithmExecutionJobUseCase) {
        this.runAlgorithmExecutionJobUseCase = runAlgorithmExecutionJobUseCase;
    }

    /**
     * Starts the algorithm job without blocking the caller thread.
     *
     * @param command job input including the pending plan identifier
     */
    @Override
    @Async
    public void runAsync(AlgorithmExecutionJobCommand command) {
        this.runAlgorithmExecutionJobUseCase.run(command);
    }
}
