package es.ull.project.domain.valueobject.time;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * PlanningPeriod
 *
 * Represents the planning horizon of an infrastructure plan,
 * such as "2026" or "2026-Q1".
 * This is a required value object in the domain.
 */
public final class PlanningPeriod {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("^\\d{4}(-Q[1-4])?$");

    private static final String ERROR_PERIOD_NOT_DEFINED = "Planning period is not defined";
    private static final String ERROR_PERIOD_FORMAT_INVALID =
            "Planning period format is invalid. Expected formats: YYYY or YYYY-Q[1-4]";

    /**
     * Value of the planning period (e.g., "2026", "2026-Q1").
     * @required
     */
    private final String value;

    /**
     * Creates a new PlanningPeriod.
     *
     * @param value Planning period value (e.g., "2026", "2026-Q1").
     */
    public PlanningPeriod(String value) {
        validateValue(value);
        this.value = value;
    }

    /**
     * Validates the planning period value.
     *
     * @param value Value to validate.
     */
    private void validateValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException(ERROR_PERIOD_NOT_DEFINED);
        }
        if (!PERIOD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(ERROR_PERIOD_FORMAT_INVALID);
        }
    }

    /**
     * Returns the planning period value.
     *
     * @return Planning period value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Checks if this planning period is equal to another one.
     *
     * @param otherObject PlanningPeriod to compare.
     * @return True if both planning periods are equal.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        PlanningPeriod other = (PlanningPeriod) otherObject;
        return this.value.equals(other.value);
    }

    /**
     * Returns the hash code of the planning period.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the string representation of the planning period.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        return String.format("PlanningPeriod={value='%s'}", this.value);
    }
}