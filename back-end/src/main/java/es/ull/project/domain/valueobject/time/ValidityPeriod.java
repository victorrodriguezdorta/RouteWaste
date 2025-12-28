package es.ull.project.domain.valueobject.time;


import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * ValidityPeriod
 *
 * Represents the period during which a plan or infrastructure is valid.
 * This is a value object with a required start date and an optional end date.
 */
public final class ValidityPeriod {

    private static final String ERROR_START_DATE_NOT_DEFINED =
            "Start date of validity period is not defined";
    private static final String ERROR_END_BEFORE_START =
            "End date of validity period cannot be before start date";

    /**
     * Start date of the validity period.
     * Required attribute.
     */
    private final LocalDate startDate;

    /**
     * End date of the validity period.
     * Optional attribute. If not present, the validity is open-ended. (CCan be null)
     */
    private final LocalDate endDate;

    /**
     * Creates a new open-ended ValidityPeriod.
     *
     * @param startDate Start date of the validity period.
     */
    public ValidityPeriod(LocalDate startDate) {
        validateStartDate(startDate);
        this.startDate = startDate;
        this.endDate = null;
    }

    /**
     * Creates a new ValidityPeriod with start and end dates.
     *
     * @param startDate Start date of the validity period.
     * @param endDate   End date of the validity period.
     */
    public ValidityPeriod(LocalDate startDate, LocalDate endDate) {
        validateStartDate(startDate);
        validateDateOrder(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Validates that the start date is defined.
     *
     * @param startDate Start date to validate.
     */
    private void validateStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException(ERROR_START_DATE_NOT_DEFINED);
        }
    }

    /**
     * Validates that the end date is not before the start date.
     *
     * @param startDate Start date.
     * @param endDate   End date.
     */
    private void validateDateOrder(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(ERROR_END_BEFORE_START);
        }
    }

    /**
     * Returns the start date of the validity period.
     *
     * @return Start date.
     */
    public LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * Returns the end date of the validity period, if defined.
     *
     * @return Optional end date.
     */
    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(this.endDate);
    }

    /**
     * Checks whether this validity period is open-ended.
     *
     * @return True if there is no end date.
     */
    public boolean isOpenEnded() {
        return this.endDate == null;
    }

    /**
     * Checks if a given date is within the validity period.
     *
     * @param date Date to check.
     * @return True if the date is within the validity period.
     */
    public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        if (this.endDate == null) {
            return !date.isBefore(this.startDate);
        }
        return !date.isBefore(this.startDate) && !date.isAfter(this.endDate);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        ValidityPeriod other = (ValidityPeriod) otherObject;
        return Objects.equals(this.startDate, other.startDate)
                && Objects.equals(this.endDate, other.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return String.format(
                "ValidityPeriod={startDate=%s, endDate=%s}",
                this.startDate,
                this.endDate != null ? this.endDate : "open-ended"
        );
    }
}