package es.ull.project.adapter.rest.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidationException
 * 
 * Exception thrown when one or more validation errors occur during
 * request deserialization or processing. Contains a list of all
 * validation errors to provide comprehensive feedback to clients.
 */
public class ValidationException extends RuntimeException {

    private static final String ERRORS_COUNT_SUFFIX = " error(s): ";
    private static final String FIELD_SEPARATOR = " - ";
    private static final String ERROR_SEPARATOR = "; ";

    private final List<FieldError> errors;
    
    /**
     * Constructs a new ValidationException with the specified list of field errors.
     * 
     * @param errors the list of field validation errors
     */
    public ValidationException(List<FieldError> errors) {
        super(buildMessage(errors));
        this.errors = new ArrayList<>(errors);
    }
    
    /**
     * Returns a copy of the list of validation errors.
     * 
     * @return a new list containing all validation errors
     */
    public List<FieldError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Builds a descriptive error message from the list of validation errors.
     * 
     * @param errors the list of field validation errors
     * @return a formatted string describing all validation errors
     */
    private static String buildMessage(List<FieldError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "Validation failed";
        }
        StringBuilder sb = new StringBuilder("Validation failed with ");
        sb.append(errors.size()).append(ERRORS_COUNT_SUFFIX);
        for (int i = 0; i < errors.size(); i++) {
            FieldError error = errors.get(i);
            sb.append(error.getField()).append(FIELD_SEPARATOR).append(error.getIssue());
            if (i < errors.size() - 1) {
                sb.append(ERROR_SEPARATOR);
            }
        }
        return sb.toString();
    }
}
