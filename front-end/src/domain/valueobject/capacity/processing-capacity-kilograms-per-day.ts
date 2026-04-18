/**
 * ProcessingCapacityKilogramsPerDay
 *
 * Represents the processing capacity of a facility in kilograms per day.
 * Immutable value object that encapsulates the daily processing capacity.
 * The unit is always kilograms per day (fixed).
 */
export class ProcessingCapacityKilogramsPerDay {
  /**
   * Processing capacity value in kilograms per day.
   * Must be non-negative.
   */
  private readonly kilogramsPerDay: number;

  /**
   * Create a new `ProcessingCapacityKilogramsPerDay`.
   * @param kilogramsPerDay The processing capacity in kilograms per day (must be >= 0)
   * @throws Error if kilogramsPerDay is negative
   */
  constructor(kilogramsPerDay: number) {
    if (kilogramsPerDay < 0) {
      throw new Error('Processing capacity cannot be negative');
    }
    this.kilogramsPerDay = kilogramsPerDay;
  }

  /**
   * Get the processing capacity value in kilograms per day.
   * @returns The processing capacity in kilograms per day
   */
  getKilogramsPerDay(): number {
    return this.kilogramsPerDay;
  }

  /**
   * Create a new instance with an updated value.
   * @param newKilogramsPerDay The new processing capacity value in kilograms per day
   * @returns A new ProcessingCapacityKilogramsPerDay instance
   * @throws Error if newKilogramsPerDay is negative
   */
  setKilogramsPerDay(newKilogramsPerDay: number): ProcessingCapacityKilogramsPerDay {
    return new ProcessingCapacityKilogramsPerDay(newKilogramsPerDay);
  }

  /**
   * Compare if this capacity is greater than another.
   * @param other The other ProcessingCapacityKilogramsPerDay to compare
   * @returns true if this capacity is greater, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThan(other: ProcessingCapacityKilogramsPerDay): boolean {
    if (!other) {
      throw new Error('Other ProcessingCapacityKilogramsPerDay cannot be null');
    }
    return this.kilogramsPerDay > other.kilogramsPerDay;
  }

  /**
   * Compare if this capacity is greater than or equal to another.
   * @param other The other ProcessingCapacityKilogramsPerDay to compare
   * @returns true if this capacity is greater or equal, false otherwise
   * @throws Error if other is null or undefined
   */
  greaterThanOrEqual(other: ProcessingCapacityKilogramsPerDay): boolean {
    if (!other) {
      throw new Error('Other ProcessingCapacityKilogramsPerDay cannot be null');
    }
    return this.kilogramsPerDay >= other.kilogramsPerDay;
  }

  /**
   * Compare if this capacity is less than another.
   * @param other The other ProcessingCapacityKilogramsPerDay to compare
   * @returns true if this capacity is less, false otherwise
   * @throws Error if other is null or undefined
   */
  lessThan(other: ProcessingCapacityKilogramsPerDay): boolean {
    if (!other) {
      throw new Error('Other ProcessingCapacityKilogramsPerDay cannot be null');
    }
    return this.kilogramsPerDay < other.kilogramsPerDay;
  }

  /**
   * Compare equality with another object.
   * @param otherObject The object to compare with
   * @returns true if the objects are equal, false otherwise
   */
  equals(otherObject: unknown): boolean {
    if (this === otherObject) {
      return true;
    }
    if (!(otherObject instanceof ProcessingCapacityKilogramsPerDay)) {
      return false;
    }
    return this.kilogramsPerDay === otherObject.kilogramsPerDay;
  }

  /**
   * Get the hash code for this object.
   * @returns A hash code based on the kilogramsPerDay value
   */
  hashCode(): number {
    return this.kilogramsPerDay !== 0 ? this.kilogramsPerDay : 0;
  }

  /**
   * Get the string representation of this object.
   * @returns A string representation
   */
  toString(): string {
    return `ProcessingCapacityKilogramsPerDay={kilogramsPerDay=${this.kilogramsPerDay}}`;
  }
}
