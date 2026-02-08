package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * VehicleType
 *
 * Represents the functional type of a vehicle within the system.
 * Each vehicle type defines how the vehicle is intended to be used.
 * COLLECTION_TRUCK, TRANSFER_TRUCK, or SUPPORT_VEHICLE.
 */
public enum VehicleType {

    /**
     * Vehicle used for direct waste collection from containers.
     */
    COLLECTION_TRUCK,

    /**
     * Vehicle used to transport waste between facilities
     * (e.g., from transfer station to treatment plant).
     */
    TRANSFER_TRUCK,

    /**
     * Support vehicle used for auxiliary or operational tasks.
     */
    SUPPORT_VEHICLE;

    /**
     * Returns the VehicleType that matches the given string.
     *
     * @param stringToCheck String representation of the type.
     * @return VehicleType matching the string.
     * @throws IllegalArgumentException if the string is null or invalid.
     */
    public static VehicleType fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException();
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (VehicleType vehicleType : values()) {
            if (vehicleType.name().equals(stringToCheck)) {
                return vehicleType;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the ordinal index of the given vehicle type string.
     *
     * @param stringToCheck String representation of the type.
     * @return Ordinal index of the vehicle type.
     */
    public static int indexOf(String stringToCheck) {
        return VehicleType
                .fromString(stringToCheck)
                .ordinal();
    }

    /**
     * Checks whether the given string is a valid VehicleType.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (VehicleType vehicleType : values()) {
            if (vehicleType.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random VehicleType.
     * 
     * Useful for testing purposes.
     *
     * @return Random VehicleType.
     */
    public static VehicleType random() {
        return values()[new Random().nextInt(values().length)];
    }
}