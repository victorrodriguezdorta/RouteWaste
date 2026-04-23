package es.ull.project.application.exception;

/**
 * AlgorithmExecutionException
 *
 * Exception used when the external algorithm process cannot be executed or its
 * response cannot be processed correctly.
 */
public class AlgorithmExecutionException extends RuntimeException {

    /**
     * Creates a new exception with a message.
     *
     * @param message error message
     */
    public AlgorithmExecutionException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with a message and a cause.
     *
     * @param message error message
     * @param cause original cause
     */
    public AlgorithmExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
