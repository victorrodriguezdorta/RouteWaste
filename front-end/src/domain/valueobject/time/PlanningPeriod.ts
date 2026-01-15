/**
 * PlanningPeriod
 *
 * Represents the planning horizon (e.g., "2026" or "2026-Q1").
 */
export class PlanningPeriod {
  private static readonly PERIOD_REGEX = /^\d{4}(-Q[1-4])?$/;
  private static readonly ERROR_NOT_DEFINED = 'Planning period is not defined';
  private static readonly ERROR_FORMAT_INVALID = 'Planning period format is invalid. Expected formats: YYYY or YYYY-Q[1-4]';

  readonly value: string;

  /**
   * Create a PlanningPeriod.
   * @param value string in format YYYY or YYYY-Q[1-4]
   */
  constructor(value: string) {
    if (value == null) throw new Error(PlanningPeriod.ERROR_NOT_DEFINED);
    if (!PlanningPeriod.PERIOD_REGEX.test(value)) throw new Error(PlanningPeriod.ERROR_FORMAT_INVALID);
    this.value = value;
  }

  /** Get raw planning period string. */
  getValue(): string { return this.value; }

  /** Equality by value string. */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof PlanningPeriod)) return false;
    return this.value === other.value;
  }

  /** Human-readable representation. */
  toString(): string { return `PlanningPeriod={value='${this.value}'}`; }
}
