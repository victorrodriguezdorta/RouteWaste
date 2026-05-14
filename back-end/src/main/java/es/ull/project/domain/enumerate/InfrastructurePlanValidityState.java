package es.ull.project.domain.enumerate;

/**
 * Indicates whether an infrastructure plan still matches the master data that was used
 * when it was produced, or whether underlying entities have changed since execution.
 */
public enum InfrastructurePlanValidityState {

    /**
     * No conflicting edits detected for referenced entities (or not yet evaluated).
     */
    VALID,

    /**
     * At least one referenced facility, vehicle, or container was edited after the plan was stored.
     */
    OBSOLETE;

    /**
     * Parses a persisted string value into {@link InfrastructurePlanValidityState}.
     *
     * @param raw stored representation (typically enum name)
     * @return matching state, or {@link #VALID} when unknown or blank (for legacy documents)
     */
    public static InfrastructurePlanValidityState fromStoredString(String raw) {
        if (raw == null || raw.isBlank()) {
            return VALID;
        }
        String normalized = raw.trim().toUpperCase();
        for (InfrastructurePlanValidityState value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        return VALID;
    }
}
