package es.ull.project.domain.enumerate;

/**
 * Computed status exposed for an infrastructure plan response.
 */
public enum InfrastructurePlanStatus {

    /**
     * The estimated cost is within the configured budget.
     */
    SUBOPTIMAL,

    /**
     * The estimated cost exceeds the configured budget.
     */
    OVERBUDGET
    ;

    private static final String ERROR_STATUS_NOT_DEFINED = "Infrastructure plan status is not defined";
    private static final String ERROR_STATUS_INVALID = "Infrastructure plan status is invalid";

    /**
     * Returns the infrastructure plan status that matches the given string.
     *
     * @param stringToCheck string representation of the status
     * @return status matching the string
     */
    public static InfrastructurePlanStatus fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException(ERROR_STATUS_NOT_DEFINED);
        }
        String normalized = stringToCheck.trim().toUpperCase();
        for (InfrastructurePlanStatus status : values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException(ERROR_STATUS_INVALID);
    }

    /**
     * Returns the index of the status with the given name.
     *
     * @param stringToCheck string representation of the status
     * @return zero-based enum index
     */
    public static int indexOf(String stringToCheck) {
        InfrastructurePlanStatus status = fromString(stringToCheck);
        InfrastructurePlanStatus[] statuses = values();
        for (int index = 0; index < statuses.length; index++) {
            if (statuses[index] == status) {
                return index;
            }
        }
        throw new IllegalArgumentException(ERROR_STATUS_INVALID);
    }

    /**
     * Checks whether the given string is a valid status.
     *
     * @param stringToCheck string to validate
     * @return true if the value matches a status, false otherwise
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        String normalized = stringToCheck.trim().toUpperCase();
        for (InfrastructurePlanStatus status : values()) {
            if (status.name().equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a deterministic sample status for tests and seed data.
     *
     * @return status sample
     */
    public static InfrastructurePlanStatus random() {
        return SUBOPTIMAL;
    }
}
