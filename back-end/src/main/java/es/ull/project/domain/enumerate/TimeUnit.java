package es.ull.project.domain.enumerate;


import java.util.Random;

/**
 * TimeUnit
 *
 * Represents the different time units used for temporal measurements.
 * Each unit defines a specific time period granularity.
 * DAY, WEEK, MONTH, or YEAR.
 */
public enum TimeUnit {

    /**
     * Day time unit.
     */
    DAY,

    /**
     * Week time unit.
     */
    WEEK,

    /**
     * Month time unit.
     */
    MONTH,

    /**
     * Year time unit.
     */
    YEAR;

    /**
     * Returns the TimeUnit that matches the given string.
     *
     * @param stringToCheck String representation of the time unit.
     * @return TimeUnit matching the string.
     * @throws IllegalArgumentException if the string is null or invalid.
     */
    public static TimeUnit fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException("Time unit is not defined");
        }

        stringToCheck = stringToCheck.trim().toUpperCase();

        for (TimeUnit timeUnit : values()) {
            if (timeUnit.name().equals(stringToCheck)) {
                return timeUnit;
            }
        }

        throw new IllegalArgumentException("Invalid time unit");
    }

    /**
     * Checks whether the given string is a valid TimeUnit.
     *
     * @param stringToCheck String to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }

        stringToCheck = stringToCheck.trim().toUpperCase();

        for (TimeUnit timeUnit : values()) {
            if (timeUnit.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random TimeUnit.
     * Useful for testing purposes.
     *
     * @return Random TimeUnit.
     */
    public static TimeUnit random() {
        return values()[new Random().nextInt(values().length)];
    }


}