package es.ull.project.adapter.mongo.exception;

/**
 * Base exception for MongoDB persistence layer issues.
 * 
 * This exception and its subclasses represent problems that occur
 * during data persistence operations, such as connection failures,
 * data corruption, or referential integrity violations.
 */
public class PersistenceException extends RuntimeException {

    /**
     * Constructs a new PersistenceException with the specified detail message.
     * 
     * @param message the detail message
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * Constructs a new PersistenceException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
