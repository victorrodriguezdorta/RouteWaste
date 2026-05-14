package es.ull.project.domain;

import java.util.UUID;

/**
 * Helpers for detecting whether the JSON snapshot of an algorithm execution request
 * references a given entity identifier.
 */
public final class InfrastructurePlanExecutionRequestReferences {

    private InfrastructurePlanExecutionRequestReferences() {
    }

    /**
     * Returns true when {@code executionRequestJson} contains the canonical JSON string form
     * of the UUID (quoted), reducing accidental substring matches inside other tokens.
     *
     * @param executionRequestJson JSON text as received from the client when the plan was run
     * @param entityId             facility, vehicle, or container id to look for
     * @return true if the id appears in the snapshot
     */
    public static boolean containsQuotedEntityId(String executionRequestJson, UUID entityId) {
        if (executionRequestJson == null || executionRequestJson.isEmpty() || entityId == null) {
            return false;
        }
        String needle = "\"" + entityId.toString().toLowerCase() + "\"";
        return executionRequestJson.toLowerCase().contains(needle);
    }
}
