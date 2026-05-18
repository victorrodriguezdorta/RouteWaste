package es.ull.project.domain.enumerate;

/**
 * StopType
 *
 * Represents the kind of stop stored in a daily plan.
 * A stop can either visit a container or represent a return to the facility.
 */
public enum StopType {

    /**
     * Stop performed at a container.
     */
    CONTAINER,

    /**
     * Stop performed at the facility, typically to unload the vehicle.
     */
    FACILITY;

    private static final String ERROR_STOP_TYPE_NOT_DEFINED = "Stop type is not defined";
    private static final String ERROR_STOP_TYPE_INVALID = "Stop type is invalid. Allowed types: ";
    private static final String SEPARATOR = ", ";

    /**
     * Returns the StopType that matches the given string.
     *
     * @param stringToCheck String representation of the stop type.
     * @return StopType matching the string.
     */
    public static StopType fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException(ERROR_STOP_TYPE_NOT_DEFINED);
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (StopType stopType : values()) {
            if (stopType.name().equals(stringToCheck)) {
                return stopType;
            }
        }
        throw new IllegalArgumentException(ERROR_STOP_TYPE_INVALID + allowedValues());
    }

    /**
     * Checks whether the given string is a valid StopType.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (StopType stopType : values()) {
            if (stopType.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the stop type with the given name.
     *
     * @param stringToCheck string representation of the stop type
     * @return zero-based enum index
     */
    public static int indexOf(String stringToCheck) {
        StopType parsed = fromString(stringToCheck);
        StopType[] stopTypes = values();
        for (int index = 0; index < stopTypes.length; index++) {
            if (stopTypes[index] == parsed) {
                return index;
            }
        }
        throw new IllegalArgumentException(ERROR_STOP_TYPE_INVALID + allowedValues());
    }

    /**
     * Returns a deterministic sample stop type for tests and seed data.
     *
     * @return stop type sample
     */
    public static StopType random() {
        return CONTAINER;
    }

    /**
     * Returns the allowed stop type names separated by commas.
     *
     * @return allowed stop type names
     */
    private static String allowedValues() {
        StringBuilder result = new StringBuilder();
        for (StopType stopType : values()) {
            result.append(stopType.name()).append(SEPARATOR);
        }
        return result.substring(0, result.length() - 2);
    }
}