package es.ull.project.domain.enumerate;


import java.util.Random;

/**
 * WasteType
 *
 * Represents the different types of waste for collection and recycling.
 * Each type defines a specific category of waste material.
 */
public enum WasteType {

    /**
     * Organic waste.
     */
    ORGANICO,

    /**
     * Packaging and containers.
     */
    ENVASES,

    /**
     * Paper and cardboard.
     */
    PAPEL_CARTON,

    /**
     * Glass waste.
     */
    VIDRIO,

    /**
     * Remaining general waste.
     */
    RESTO;

    /**
     * Returns the WasteType that matches the given string.
     *
     * @param stringToCheck String representation of the waste type.
     * @return WasteType matching the string.
     * @throws IllegalArgumentException if the string is null or invalid.
     */
    public static WasteType fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException("Waste type is not defined");
        }

        stringToCheck = stringToCheck.trim().toUpperCase();

        for (WasteType wasteType : values()) {
            if (wasteType.name().equals(stringToCheck)) {
                return wasteType;
            }
        }

        throw new IllegalArgumentException(
                "Waste type is invalid. Allowed types: " + allowedValues()
        );
    }

    /**
     * Returns the ordinal index of the given waste type string.
     *
     * @param stringToCheck String representation of the waste type.
     * @return Ordinal index of the waste type.
     */
    public static int indexOf(String stringToCheck) {
        return WasteType
                .fromString(stringToCheck)
                .ordinal();
    }

    /**
     * Checks whether the given string is a valid WasteType.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }

        stringToCheck = stringToCheck.trim().toUpperCase();

        for (WasteType wasteType : values()) {
            if (wasteType.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random WasteType.
     * Useful for testing purposes.
     *
     * @return Random WasteType.
     */
    public static WasteType random() {
        return values()[new Random().nextInt(values().length)];
    }

    /**
     * Returns a comma-separated string of all allowed waste type values.
     *
     * @return String containing all allowed values.
     */
    private static String allowedValues() {
        StringBuilder result = new StringBuilder();
        for (WasteType wasteType : values()) {
            result.append(wasteType.name()).append(", ");
        }
        return result.substring(0, result.length() - 2);
    }
}