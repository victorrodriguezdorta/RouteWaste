package es.ull.project.domain.enumerate;

/**
 * Lifecycle state of an infrastructure plan while the delivery algorithm runs asynchronously.
 */
public enum InfrastructurePlanExecutionState {

    /**
     * Placeholder persisted; algorithm execution is in progress.
     */
    RUNNING,

    /**
     * Algorithm finished and plan data was persisted successfully.
     */
    COMPLETED,

    /**
     * Algorithm or persistence failed after the placeholder was created.
     */
    FAILED;

    private static final String ERROR_STATE_NOT_DEFINED = "Infrastructure plan execution state is not defined";
    private static final String ERROR_STATE_INVALID = "Infrastructure plan execution state is invalid";

    /**
     * Parses a persisted string value into {@link InfrastructurePlanExecutionState}.
     *
     * @param raw stored representation (typically enum name)
     * @return matching state, or {@link #COMPLETED} when unknown or blank (for legacy documents)
     */
    public static InfrastructurePlanExecutionState fromStoredString(String raw) {
        if (raw == null || raw.isBlank()) {
            return COMPLETED;
        }
        String normalized = raw.trim().toUpperCase();
        for (InfrastructurePlanExecutionState value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        return COMPLETED;
    }

    /**
     * Returns the execution state that matches the given string.
     *
     * @param stringToCheck string representation of the execution state
     * @return execution state matching the string
     */
    public static InfrastructurePlanExecutionState fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException(ERROR_STATE_NOT_DEFINED);
        }
        String normalized = stringToCheck.trim().toUpperCase();
        for (InfrastructurePlanExecutionState value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        throw new IllegalArgumentException(ERROR_STATE_INVALID);
    }

    /**
     * Returns the index of the execution state with the given name.
     *
     * @param stringToCheck string representation of the execution state
     * @return zero-based enum index
     */
    public static int indexOf(String stringToCheck) {
        InfrastructurePlanExecutionState state = fromString(stringToCheck);
        InfrastructurePlanExecutionState[] states = values();
        for (int index = 0; index < states.length; index++) {
            if (states[index] == state) {
                return index;
            }
        }
        throw new IllegalArgumentException(ERROR_STATE_INVALID);
    }

    /**
     * Checks whether the given string is a valid execution state.
     *
     * @param stringToCheck string to validate
     * @return true if the value matches an execution state, false otherwise
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        String normalized = stringToCheck.trim().toUpperCase();
        for (InfrastructurePlanExecutionState value : values()) {
            if (value.name().equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a deterministic sample execution state for tests and seed data.
     *
     * @return execution state sample
     */
    public static InfrastructurePlanExecutionState random() {
        return COMPLETED;
    }
}
