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
    public String error;
    public String message;
    public List<FieldError> details;
}
