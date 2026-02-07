package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * ServiceZone
 *
 * Represents the different types of geographical service zones for waste collection.
 * Each zone defines the scope and boundaries of service areas.
 * NEIGHBORHOOD, DISTRICT, or GEOGRAPHICAL_AREA.
 */
public enum ServiceZone {

    /**
     * Neighborhood-level service zone.
     */
    NEIGHBORHOOD,

    /**
     * District-level service zone.
     */
    DISTRICT,

    /**
     * Geographical area service zone.
     */
    GEOGRAPHICAL_AREA;

    private static final String ERROR_SERVICE_ZONE_NOT_DEFINED = "Service zone is not defined";
    private static final String ERROR_SERVICE_ZONE_INVALID = "Service zone is invalid. Allowed zones: ";

    /**
     * Returns the ServiceZone that matches the given string.
     *
     * @param stringToCheck String representation of the service zone.
     * @return ServiceZone matching the string.
     * @throws IllegalArgumentException if the string is null or invalid.
     */
    public static ServiceZone fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException(ERROR_SERVICE_ZONE_NOT_DEFINED);
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (ServiceZone serviceZone : values()) {
            if (serviceZone.name().equals(stringToCheck)) {
                return serviceZone;
            }
        }
        throw new IllegalArgumentException(
                ERROR_SERVICE_ZONE_INVALID + allowedValues()
        );
    }

    /**
     * Returns the ordinal index of the given service zone string.
     *
     * @param stringToCheck String representation of the service zone.
     * @return Ordinal index of the service zone.
     */
    public static int indexOf(String stringToCheck) {
        return ServiceZone
                .fromString(stringToCheck)
                .ordinal();
    }

    /**
     * Checks whether the given string is a valid ServiceZone.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (ServiceZone serviceZone : values()) {
            if (serviceZone.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random ServiceZone.
     * Useful for testing purposes.
     *
     * @return Random ServiceZone.
     */
    public static ServiceZone random() {
        return values()[new Random().nextInt(values().length)];
    }

    /**
     * Returns a comma-separated string of all allowed service zone values.
     *
     * @return String containing all allowed values.
     */
    private static String allowedValues() {
        StringBuilder result = new StringBuilder();
        for (ServiceZone serviceZone : values()) {
            result.append(serviceZone.name()).append(", ");
        }
        return result.substring(0, result.length() - 2);
    }
}