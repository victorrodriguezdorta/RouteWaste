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
    
    public ErrorResponse(String error, String message, List<FieldError> details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }
    
    public String getError() {
        return error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public List<FieldError> getDetails() {
        return details;
    }
}
