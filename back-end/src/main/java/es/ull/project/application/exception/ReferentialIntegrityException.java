package es.ull.project.application.exception;

import java.util.List;
import java.util.UUID;

/**
 * Exception thrown when attempting to delete an entity that is still referenced by other entities.
 * This prevents orphaned references and maintains referential integrity in the system.
 */
public class ReferentialIntegrityException extends RuntimeException {

    private final String entityType;
    private final String entityId;
    private final String referencingType;
    private final List<UUID> referencingIds;

    /**
     * Constructs a new ReferentialIntegrityException.
     *
     * @param entityType the type of entity being deleted
     * @param entityId the ID of the entity being deleted
     * @param referencingType the type of entities that reference this entity
     * @param referencingIds the list of IDs of entities that reference this entity
     */
    public ReferentialIntegrityException(String entityType, String entityId, String referencingType, List<UUID> referencingIds) {
        super(String.format("Cannot delete %s %s: referenced by %d %s(s). Please delete referencing entities first.",
                entityType, entityId, referencingIds.size(), referencingType));
        this.entityType = entityType;
        this.entityId = entityId;
        this.referencingType = referencingType;
        this.referencingIds = referencingIds;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getReferencingType() {
        return referencingType;
    }

    public List<UUID> getReferencingIds() {
        return referencingIds;
    }

    public long getReferenceCount() {
        return referencingIds.size();
    }
}
