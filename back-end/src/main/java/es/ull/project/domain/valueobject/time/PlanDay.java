package es.ull.project.domain.valueobject.time;

import java.util.Objects;

/**
 * PlanDay
 *
 * Value object representing a specific day within a planning horizon.
 */
public class PlanDay {

    /**
     * The day number.
     */
    private final Integer day;

    /**
     * Creates a PlanDay.
     *
     * @param day the day number
     */
    public PlanDay(Integer day) {
        if (day == null || day < 0) {
            throw new IllegalArgumentException("Plan day must be a non-negative integer");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanDay planDay = (PlanDay) o;
        return Objects.equals(day, planDay.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }

    @Override
    public String toString() {
        return String.valueOf(day);
    }
}
