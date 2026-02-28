package es.ull.project.adapter.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import es.ull.project.adapter.rest.exception.ValidationException;
import es.ull.project.adapter.rest.response.ErrorResponse;

/**
 * GlobalExceptionHandler
 * 
 * Global exception handler for the REST API.
 * This class uses Spring's @ControllerAdvice to intercept exceptions thrown
 * by controllers and convert them into clean, user-friendly error responses
 * without exposing internal stack traces or implementation details.
 * 
 * It handles common exception types like:
 * - Deserialization errors (malformed JSON, missing required fields)
 * - Validation errors (illegal arguments)
 * - Entity not found errors
 * 
 * All error responses follow a consistent format with HTTP status codes
 * and clear error messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles deserialization errors when the request body cannot be parsed.
     * This typically occurs when:
     * - JSON syntax is invalid
     * - Required fields are missing
     * - Field values don't match expected types
     * - Custom validation in deserializers fails
     * 
     * @param ex the HttpMessageNotReadableException thrown by Jackson
     * @return ResponseEntity with error message and HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleDeserializationError(HttpMessageNotReadableException ex) {
        String message = extractRootCauseMessage(ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors with detailed field-level error information.
     * This provides structured feedback about all validation failures,
     * allowing clients to display comprehensive error messages.
     * 
     * @param ex the ValidationException containing multiple field errors
     * @return ResponseEntity with structured error response and HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.error = "ValidationError";
        errorResponse.message = "Validation failed";
        errorResponse.details = ex.getErrors();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors and illegal arguments.
     * This typically occurs when:
     * - Business rule validation fails
     * - Referenced entities (by ID) don't exist
     * - Invalid values are provided for domain objects
     * 
     * @param ex the IllegalArgumentException thrown by services or domain entities
     * @return ResponseEntity with error message and HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles illegal state errors.
     * This typically occurs when:
     * - Business invariant violations (e.g., facility capacity exceeded)
     * - Operations on entities in invalid states (e.g., discarded facilities)
     * - Domain logic constraints are violated
     * 
     * @param ex the IllegalStateException thrown by domain entities
     * @return ResponseEntity with error message and HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateError(IllegalStateException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles entity not found errors.
     * This typically occurs when:
     * - A GET/PUT/DELETE request references a non-existent ID
     * - A repository lookup returns empty
     * 
     * @param ex the NoSuchElementException thrown by services
     * @return ResponseEntity with error message and HTTP 404 (NOT_FOUND)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundError(NoSuchElementException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage() != null ? ex.getMessage() : "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles any other unexpected exceptions.
     * This is a catch-all handler that prevents internal error details
     * from being exposed to clients.
     * 
     * @param ex the generic Exception
     * @return ResponseEntity with generic error message and HTTP 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericError(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An internal error occurred. Please try again later.");
        logger.error("Unhandled exception: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts the root cause message from a chained exception.
     * This method traverses the exception chain to find the original
     * cause and its message, which typically contains the most relevant
     * error information for the user.
     * 
     * @param ex the exception to extract the root cause from
     * @return the message from the root cause, or the original message if no cause exists
     */
    private String extractRootCauseMessage(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        String message = cause.getMessage();
        return message != null ? message : "Invalid request format";
    }
}
