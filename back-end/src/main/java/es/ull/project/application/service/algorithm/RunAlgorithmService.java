package es.ull.project.application.service.algorithm;

import es.ull.project.application.port.algorithm.AlgorithmRunner;
import es.ull.project.application.usecase.algorithm.RunAlgorithmUseCase;

/**
 * Service implementation for running the external algorithm process.
 */
public class RunAlgorithmService implements RunAlgorithmUseCase {

    private final AlgorithmRunner algorithmRunner;

    /**
     * Creates a new service with the provided algorithm runner.
     *
     * @param algorithmRunner adapter used to run the algorithm process
     */
    public RunAlgorithmService(AlgorithmRunner algorithmRunner) {
        this.algorithmRunner = algorithmRunner;
    }

    /**
     * Executes the algorithm process with the provided JSON payload.
     *
     * @param processedJson JSON payload to send to the algorithm
     * @return raw JSON returned by the algorithm
     */
    @Override
    public String execute(String processedJson) {
        return this.algorithmRunner.run(processedJson);
    }
}
