package es.ull.project.application.repository.algorithm;

/**
 * AlgorithmRunner
 *
 * Defines the contract required to execute the external algorithm process and
 * retrieve the JSON produced by it.
 */
public interface AlgorithmRunner {

    /**
     * Executes the external algorithm with the provided processed JSON payload.
     *
     * @param processedJson JSON payload that will be passed to the algorithm
     * @return raw JSON returned by the algorithm process
     */
    String run(String processedJson);
}
