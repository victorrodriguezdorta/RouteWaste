package es.ull.project.application.usecase.algorithm;

import es.ull.project.domain.valueobject.algorithm.AlgorithmJsonPayload;

/**
 * Use case for running the external algorithm process.
 */
public interface RunAlgorithmUseCase {

    /**
     * Executes the algorithm process with the provided JSON payload.
     *
     * @param processedJson JSON payload to send to the algorithm
     * @return raw JSON returned by the algorithm
     */
    AlgorithmJsonPayload execute(AlgorithmJsonPayload processedJson);
}
