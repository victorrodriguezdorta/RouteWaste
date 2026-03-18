/**
 * ValidityPeriod
 *
 * Represents a period with required start date and optional end date.
 */
export class ValidityPeriod {
  /** Error message when start date is not defined. */
  private static readonly ERROR_START_NOT_DEFINED = 'Start date of validity period is not defined';
  /** Error message when end date is before start date. */
  private static readonly ERROR_END_BEFORE_START = 'End date of validity period cannot be before start date';

  /** The start date of the validity period. */
  readonly startDate: Date;
  /** The optional end date of the validity period. */
  readonly endDate?: Date | null;

  /**
   * Create a ValidityPeriod.
   * @param startDate required start date
   * @param endDate optional end date (must not be before start)
   */
  constructor(startDate: Date, endDate?: Date | null) {
    if (!startDate) throw new Error(ValidityPeriod.ERROR_START_NOT_DEFINED);
    if (endDate && endDate < startDate) throw new Error(ValidityPeriod.ERROR_END_BEFORE_START);
    this.startDate = startDate;
    this.endDate = endDate ?? null;
  }

  /**
   * Return true if the period has no end date.
   * @returns True if the period is open-ended, false otherwise.
   */
  isOpenEnded(): boolean { return this.endDate == null; }

  /**
   * Check whether a date is within the validity period (inclusive).
   * @param date date to check
   * @returns True if the date is within the period, false otherwise.
   */
  contains(date: Date | null | undefined): boolean {
    if (!date) return false;
    if (this.endDate == null) return date >= this.startDate;
    return date >= this.startDate && date <= this.endDate;
  }

  /**
   * Equality comparing start and end dates.
   * @param other The object to compare with.
   * @returns True if both periods have the same start and end dates, false otherwise.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ValidityPeriod)) return false;
    const o = other as ValidityPeriod;
    const endEq = (this.endDate == null && o.endDate == null) || (this.endDate != null && o.endDate != null && this.endDate.getTime() === o.endDate.getTime());
    return this.startDate.getTime() === o.startDate.getTime() && endEq;
  }

  /**
   * Human-readable date-range representation (ISO YYYY-MM-DD).
   * @returns A string representation of the validity period.
   */
  toString(): string {
    return `ValidityPeriod={startDate=${this.startDate.toISOString().slice(0,10)}, endDate=${this.endDate ? this.endDate.toISOString().slice(0,10) : 'open-ended'}}`;
  }
}
