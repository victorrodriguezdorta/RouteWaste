package es.ull.project.adapter.rest.exception;

/**
 * FieldError
 * 
 * Represents a single field validation error with details about
 * which field failed validation and why.
 */
public class FieldError {
    private final String field;
    private final String issue;
    
    public FieldError(String field, String issue) {
        this.field = field;
        this.issue = issue;
    }
    
    public String getField() {
        return field;
    }
    
    public String getIssue() {
        return issue;
    }
}
