package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * FacilityType
 *
 * Represents the different types of waste management facilities.
 * Each type defines the specific function and purpose of the facility.
 * OPERATIONAL_BASE, TRANSFER_STATION, or TREATMENT_PLANT.
 */
public enum FacilityType {

    /**
     * Operational base for waste collection vehicles and operations.
     */
    OPERATIONAL_BASE,

    /**
     * Transfer station for temporary waste storage and distribution.
     */
    TRANSFER_STATION,

    /**
     * Treatment plant for waste processing and final disposal.
     */
    TREATMENT_PLANT;

    private static final String ERROR_FACILITY_TYPE_NOT_DEFINED = "Facility type is not defined";
    private static final String ERROR_FACILITY_TYPE_INVALID = "Facility type is invalid. Allowed types: ";

    /**
     * Returns the FacilityType that matches the given string.
     *
     * @param stringToCheck String representation of the facility type.
     * @return FacilityType matching the string.
     * @throws IllegalArgumentException if the string is null or invalid.
     */
    public static FacilityType fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException(ERROR_FACILITY_TYPE_NOT_DEFINED);
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (FacilityType facilityType : values()) {
            if (facilityType.name().equals(stringToCheck)) {
                return facilityType;
            }
        }
        throw new IllegalArgumentException(
                ERROR_FACILITY_TYPE_INVALID + allowedValues());
    }

    /**
     * Returns the ordinal index of the given facility type string.
     *
     * @param stringToCheck String representation of the facility type.
     * @return Ordinal index of the facility type.
     */
    public static int indexOf(String stringToCheck) {
        return FacilityType
                .fromString(stringToCheck)
                .ordinal();
    }

    /**
     * Checks whether the given string is a valid FacilityType.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (FacilityType facilityType : values()) {
            if (facilityType.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random FacilityType.
     * Useful for testing purposes.
     *
     * @return Random FacilityType.
     */
    public static FacilityType random() {
        return values()[new Random().nextInt(values().length)];
    }

    /**
     * Returns a comma-separated string of all allowed facility type values.
     *
     * @return String containing all allowed values.
     */
    private static String allowedValues() {
        StringBuilder result = new StringBuilder();
        for (FacilityType facilityType : values()) {
            result.append(facilityType.name()).append(", ");
        }
        return result.substring(0, result.length() - 2);
    }
}