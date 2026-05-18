package es.ull.project.domain.enumerate;

/**
 * ContainerStatus
 *
 * Enumeration representing the monitoring status of a container for a given day.
 */
public enum ContainerStatus {
    CORRECT,
    OVERFLOWED;

    private static final String ERROR_STATUS_NOT_DEFINED = "Container status is not defined";
    private static final String ERROR_STATUS_INVALID = "Container status is invalid";

    /**
     * Selects a random container status.
     *
     * @return random container status
     */
    public static ContainerStatus random() {
        ContainerStatus[] values = values();
        return values[(int) (Math.random() * values.length)];
    }

    /**
     * Parses a container status from text.
     *
     * @param value text value to parse
     * @return parsed status, or {@link #CORRECT} when the value is null or invalid
     */
    public static ContainerStatus fromString(String value) {
        if (value == null) {
            return CORRECT;
        }
        try {
            return ContainerStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return CORRECT;
        }
    }

    /**
     * Returns the index of the container status with the given name.
     *
     * @param value text value to parse
     * @return zero-based enum index
     */
    public static int indexOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_STATUS_NOT_DEFINED);
        }
        String normalized = value.trim().toUpperCase();
        ContainerStatus[] statuses = values();
        for (int index = 0; index < statuses.length; index++) {
            if (statuses[index].name().equals(normalized)) {
                return index;
            }
        }
        throw new IllegalArgumentException(ERROR_STATUS_INVALID);
    }

    /**
     * Checks whether the given text matches a container status.
     *
     * @param value text value to validate
     * @return true if the value matches a status, false otherwise
     */
    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toUpperCase();
        for (ContainerStatus status : values()) {
            if (status.name().equals(normalized)) {
                return true;
            }
        }
        return false;
    }
}
