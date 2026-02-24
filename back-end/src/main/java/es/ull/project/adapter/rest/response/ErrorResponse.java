package es.ull.project.adapter.rest.response;

import java.util.List;

import es.ull.project.adapter.rest.exception.FieldError;

/**
 * ErrorResponse
 * 
 * Standard error response structure for validation errors.
 * Provides detailed information about all validation failures
 * in a structured, client-friendly format.
 */
public class ErrorResponse {
    private final String error;
    private final String message;
    private final List<FieldError> details;
    
    /**
     * Constructs an ErrorResponse with error information and validation details.
     * @param error the error type or category
     * @param message the human-readable error message
     * @param details the list of field-specific validation errors
     */
    public ErrorResponse(String error, String message, List<FieldError> details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }
    
    /**
     * Gets the error type or category.
     * @return the error type
     */
    public String getError() {
        return error;
    }
    
    /**
     * Gets the human-readable error message.
     * @return the error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the list of field-specific validation errors.
     * @return the list of validation errors
     */
    public List<FieldError> getDetails() {
        return details;
    }
}
