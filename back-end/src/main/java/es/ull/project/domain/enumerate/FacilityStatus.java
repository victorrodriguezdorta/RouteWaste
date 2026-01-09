package es.ull.project.domain.enumerate;


import java.util.Random;

/**
 * FacilityStatus
 *
 * Represents the current lifecycle state of a facility.
 * It defines whether a facility can be used to assign containers.
 * CANDIDATE, PLANNED, OPEN, or DISCARDED.
 */
public enum FacilityStatus {

    /**
     * Facility is a candidate and not yet selected.
     */
    CANDIDATE,

    /**
     * Facility is planned but not yet open.
     */
    PLANNED,

    /**
     * Facility is open and operational.
     */
    OPEN,

    /**
     * Facility has been discarded and cannot be used.
     */
    DISCARDED;

    /**
     * Returns the FacilityStatus that matches the given string.
     *
     * @param stringToCheck String representation of the status.
     * @return FacilityStatus matching the string.
     * @throws IllegalArgumentException if the string is null or invalid.
     */
    public static FacilityStatus fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException();
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (FacilityStatus status : values()) {
            if (status.name().equals(stringToCheck)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the ordinal index of the given status string.
     *
     * @param stringToCheck String representation of the status.
     * @return Ordinal index of the status.
     */
    public static int indexOf(String stringToCheck) {
        return FacilityStatus
                .fromString(stringToCheck)
                .ordinal();
    }

    /**
     * Checks whether the given string is a valid FacilityStatus.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (FacilityStatus status : values()) {
            if (status.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random FacilityStatus.
     * Useful for testing purposes.
     *
     * @return Random FacilityStatus.
     */
    public static FacilityStatus random() {
        return values()[new Random().nextInt(values().length)];
    }

    /**
     * Checks whether this status allows service assignments.
     *
     * @return True if containers can be assigned to the facility.
     */
    public boolean allowsServiceAssignments() {
        return this == PLANNED || this == OPEN;
    }

    /**
     * Checks whether this status is DISCARDED.
     *
     * @return True if the facility is discarded.
     */
    public boolean isDiscarded() {
        return this == DISCARDED;
    }
}