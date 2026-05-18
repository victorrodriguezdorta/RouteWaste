package es.ull.project.domain.valueobject.time;

import java.util.Objects;

/**
 * PlanDay
 *
 * Value object representing a specific day within a planning horizon.
 */
public class PlanDay {

    private static final int MINIMUM_DAY = 0;
    private static final String PLAN_DAY_NEGATIVE = "Plan day must be a non-negative integer";

    /**
     * The day number.
     * It is a required attribute.
     */
    private final Integer day;

    /**
     * Creates a PlanDay.
     *
     * @param day the day number
     */
    public PlanDay(Integer day) {
        if (day == null || day < MINIMUM_DAY) {
            throw new IllegalArgumentException(PLAN_DAY_NEGATIVE);
        }
        this.day = day;
    }

    /**
     * Creates a PlanDay from an Integer.
     *
     * @param day the day number
     * @return the PlanDay
     */
    public static PlanDay fromInteger(Integer day) {
        return new PlanDay(day);
    }

    /**
     * Gets the day number.
     *
     * @return the day number
     */
    public Integer getDay() {
        return day;
    }

    /**
     * Compares this plan day with another object.
     *
     * @param otherObject object to compare with
     * @return true when both plan days are equal
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        PlanDay planDay = (PlanDay) otherObject;
        return Objects.equals(day, planDay.day);
    }

    /**
     * Returns the hash code for this plan day.
     *
     * @return hash code based on the day number
     */
    @Override
    public int hashCode() {
        return Objects.hash(day);
    }

    /**
     * Returns the plan day as text.
     *
     * @return day number string
     */
    @Override
    public String toString() {
        return String.valueOf(day);
    }
}
