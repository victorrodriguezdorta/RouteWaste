package es.ull.project.domain.enumerate;

/**
 * Overall status of an infrastructure plan: while the algorithm runs, when it is up to date,
 * or when underlying master data has changed since it was produced.
 */
public enum InfrastructurePlanValidityState {

    /**
     * Algorithm execution is in progress; plan data in persistence is still a placeholder.
     */
    RUNNING,

    /**
     * No conflicting edits detected for referenced entities (or not yet evaluated).
     */
    VALID,

    /**
     * At least one referenced facility, vehicle, or container was edited after the plan was stored.
     */
    OBSOLETE;

    private static final String ERROR_STATE_NOT_DEFINED = "Infrastructure plan validity state is not defined";
    private static final String ERROR_STATE_INVALID = "Infrastructure plan validity state is invalid";

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

    /**
     * Returns the validity state that matches the given string.
     *
     * @param stringToCheck string representation of the validity state
     * @return validity state matching the string
     */
    public static InfrastructurePlanValidityState fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException(ERROR_STATE_NOT_DEFINED);
        }
        String normalized = stringToCheck.trim().toUpperCase();
        for (InfrastructurePlanValidityState value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        throw new IllegalArgumentException(ERROR_STATE_INVALID);
    }

    /**
     * Returns the index of the validity state with the given name.
     *
     * @param stringToCheck string representation of the validity state
     * @return zero-based enum index
     */
    public static int indexOf(String stringToCheck) {
        InfrastructurePlanValidityState state = fromString(stringToCheck);
        InfrastructurePlanValidityState[] states = values();
        for (int index = 0; index < states.length; index++) {
            if (states[index] == state) {
                return index;
            }
        }
        throw new IllegalArgumentException(ERROR_STATE_INVALID);
    }

    /**
     * Checks whether the given string is a valid validity state.
     *
     * @param stringToCheck string to validate
     * @return true if the value matches a validity state, false otherwise
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        String normalized = stringToCheck.trim().toUpperCase();
        for (InfrastructurePlanValidityState value : values()) {
            if (value.name().equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a deterministic sample validity state for tests and seed data.
     *
     * @return validity state sample
     */
    public static InfrastructurePlanValidityState random() {
        return VALID;
    }
}
