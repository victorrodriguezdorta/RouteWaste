package es.ull.project.adapter.mongo.exception;

/**
 * Exception thrown when data retrieved from the database is corrupted or invalid.
 * 
 * This can occur when:
 * - Document fields contain null values when non-null is expected
 * - String values cannot be parsed to expected types (e.g., invalid UUID format)
 * - Enum values don't match expected constants
 * - Required nested objects are missing
 */
public class DataCorruptionException extends PersistenceException {

    private final String documentType;
    private final String fieldName;
    private final Object invalidValue;

    /**
     * Constructs a new DataCorruptionException with a simple message.
     * 
     * @param message the detail message
     */
    public DataCorruptionException(String message) {
        super(message);
        this.documentType = null;
        this.fieldName = null;
        this.invalidValue = null;
    }

    /**
     * Constructs a new DataCorruptionException with detailed field information.
     * 
     * @param documentType the type of document containing corrupted data
     * @param fieldName the name of the corrupted field
     * @param invalidValue the invalid value found
     * @param message additional detail message
     */
    public DataCorruptionException(String documentType, String fieldName, Object invalidValue, String message) {
        super(String.format("Data corruption in %s.%s (value: %s): %s", 
                documentType, fieldName, invalidValue, message));
        this.documentType = documentType;
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    /**
     * Constructs a new DataCorruptionException with a cause.
     * 
     * @param message the detail message
     * @param cause the underlying cause
     */
    public DataCorruptionException(String message, Throwable cause) {
        super(message, cause);
        this.documentType = null;
        this.fieldName = null;
        this.invalidValue = null;
    }

    /**
     * Returns the type of document containing corrupted data.
     * 
     * @return the document type, or null if not specified
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Returns the name of the corrupted field.
     * 
     * @return the field name, or null if not specified
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the invalid value that was found.
     * 
     * @return the invalid value, or null if not specified
     */
    public Object getInvalidValue() {
        return invalidValue;
    }
}
