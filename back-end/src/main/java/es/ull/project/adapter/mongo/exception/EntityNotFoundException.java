package es.ull.project.adapter.mongo.exception;

import java.util.UUID;

/**
 * Exception thrown when a referenced entity cannot be found in the database.
 * 
 * This typically occurs when there's a referential integrity issue,
 * such as a ServiceAssignment pointing to a non-existent Container or Facility.
 */
public class EntityNotFoundException extends PersistenceException {

    private final String entityType;
    private final UUID entityId;

    /**
     * Constructs a new EntityNotFoundException.
     * 
     * @param entityType the type of entity that was not found (e.g., "Container", "Facility")
     * @param entityId the ID of the entity that was not found
     */
    public EntityNotFoundException(String entityType, UUID entityId) {
        super(String.format("%s with ID %s not found in database", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Constructs a new EntityNotFoundException with an additional context message.
     * 
     * @param entityType the type of entity that was not found
     * @param entityId the ID of the entity that was not found
     * @param context additional context about where the error occurred
     */
    public EntityNotFoundException(String entityType, UUID entityId, String context) {
        super(String.format("%s with ID %s not found in database (context: %s)", entityType, entityId, context));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Returns the type of entity that was not found.
     * 
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Returns the ID of the entity that was not found.
     * 
     * @return the entity ID
     */
    public UUID getEntityId() {
        return entityId;
    }
}
