package es.ull.project.domain.valueobject.time;

import java.time.LocalDate;

/**
 * ServiceDate
 *
 * Represents the calendar date on which a daily plan is scheduled to be executed.
 * Immutable value object wrapping a {@link LocalDate}.
 * It is a required attribute.
 */
public final class ServiceDate {

    private static final String ERROR_DATE_NULL = "Service date must not be null";

    /**
     * The service execution date.
     * It is a required attribute.
     */
    private final LocalDate date;

    /**
     * Creates a new ServiceDate.
     *
     * @param date the execution date; must not be null
     * @throws IllegalArgumentException if date is null
     */
    public ServiceDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException(ERROR_DATE_NULL);
        }
        this.date = date;
    }

    /**
     * Creates a ServiceDate from an ISO-8601 date string (e.g. {@code "2026-05-07"}).
     *
     * @param isoDate the ISO-8601 date string; must not be null or blank
     * @return the corresponding ServiceDate
     */
    public static ServiceDate of(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            throw new IllegalArgumentException(ERROR_DATE_NULL);
        }
        return new ServiceDate(LocalDate.parse(isoDate));
    }

    /**
     * Returns the underlying {@link LocalDate}.
     *
     * @return the service date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the ISO-8601 string representation of the date (e.g. {@code "2026-05-07"}).
     *
     * @return ISO-8601 date string
     */
    public String getValue() {
        return date.toString();
    }

    /**
     * Checks equality based on the date.
     *
     * @param otherObject the object to compare with
     * @return {@code true} if the other object has the same date
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ServiceDate other = (ServiceDate) otherObject;
        return date.equals(other.date);
    }

    /**
     * Returns a hash code based on the date.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(date);
    }

    /**
     * Returns the ISO-8601 string representation.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "ServiceDate={" + date + "}";
    }
}
