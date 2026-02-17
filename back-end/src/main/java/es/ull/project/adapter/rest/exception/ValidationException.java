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
    private final List<FieldError> errors;
    
    public ValidationException(List<FieldError> errors) {
        super(buildMessage(errors));
        this.errors = new ArrayList<>(errors);
    }
    
    public List<FieldError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    private static String buildMessage(List<FieldError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "Validation failed";
        }
        
        StringBuilder sb = new StringBuilder("Validation failed with ");
        sb.append(errors.size()).append(" error(s): ");
        
        for (int i = 0; i < errors.size(); i++) {
            FieldError error = errors.get(i);
            sb.append(error.getField()).append(" - ").append(error.getIssue());
            if (i < errors.size() - 1) {
                sb.append("; ");
            }
        }
        
        return sb.toString();
    }
}
