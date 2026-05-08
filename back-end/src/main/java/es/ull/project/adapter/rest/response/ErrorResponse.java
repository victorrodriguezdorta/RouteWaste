package es.ull.project.adapter.rest.response;

import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.domain.valueobject.error.ErrorCode;
import es.ull.project.domain.valueobject.error.ErrorMessage;
import java.util.List;

/**
 * ErrorResponse
 *
 * Standard error response structure for validation errors.
 * Provides detailed information about all validation failures
 * in a structured, client-friendly format.
 */
public class ErrorResponse {

    /**
     * Machine-readable error code label (e.g. "ValidationError").
     */
    public ErrorCode error;

    /**
     * Human-readable description of the error.
     */
    public ErrorMessage message;

    /**
     * Detailed list of field-level validation errors.
     */
    public List<FieldError> details;
}
