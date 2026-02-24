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
    
    /**
     * Constructs a FieldError with the specified field name and issue description.
     * 
     * @param field the name of the field that failed validation
     * @param issue the description of the validation error
     */
    public FieldError(String field, String issue) {
        this.field = field;
        this.issue = issue;
    }
    
    /**
     * Gets the name of the field that failed validation.
     * 
     * @return the field name
     */
    public String getField() {
        return field;
    }
    
    /**
     * Gets the description of the validation error.
     * 
     * @return the issue description
     */
    public String getIssue() {
        return issue;
    }
}
